import axios from "axios";
import { jwtDecode } from "jwt-decode";

// 토큰 갱신 중인지 여부를 추적하는 플래그
let isRefreshing = false;
// 토큰 갱신 완료 후 재시도할 요청들을 저장하는 배열
let refreshSubscribers: Array<(token: string) => void> = [];

// 토큰 만료 여부 확인 함수
const isTokenExpired = (token: string): boolean => {
  try {
    const decoded: any = jwtDecode(token);
    const currentTime = Date.now() / 1000;

    // 토큰 만료 10초 전에 미리 갱신 시작 (버퍼 적용)
    return decoded.exp < currentTime + 10;
  } catch (error) {
    return true; // 디코딩 실패 시 만료된 것으로 간주
  }
};

// 토큰 갱신 완료 후 대기 중인 요청들을 처리하는 함수
const onRefreshed = (newToken: string) => {
  refreshSubscribers.forEach((callback) => callback(newToken));
  refreshSubscribers = [];
};

// 토큰 갱신 실패 시 대기 중인 요청들을 모두 에러로 처리하는 함수
const onRefreshError = (error: any) => {
  refreshSubscribers.forEach((callback) => callback(""));
  refreshSubscribers = [];
  return Promise.reject(error);
};

// 토큰 갱신 중에 새로운 요청이 오면 대기시키는 함수
const addSubscriber = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback);
};

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
});

// refreshAccessToken 함수 개선
const refreshAccessToken = async (): Promise<string> => {
  try {
    const refreshToken = localStorage.getItem("refreshToken");

    if (!refreshToken) {
      throw new Error("No refresh token available");
    }

    console.log("토큰 재발급 요청 시작");

    const response = await axios.post(
        `${import.meta.env.VITE_API_BASE_URL}/reissue-token`,
        {},
        {
          headers: {
            Authorization: `Bearer ${refreshToken}`,
          },
        }
    );

    const newAccessToken = response.headers["authorization"]?.replace(
        "Bearer ",
        ""
    );
    const newRefreshToken = response.headers["refresh"];

    if (!newAccessToken) {
      throw new Error("Token refresh failed: No new access token");
    }

    localStorage.setItem("accessToken", newAccessToken);
    if (newRefreshToken) {
      localStorage.setItem("refreshToken", newRefreshToken);
    }

    console.log("토큰 재발급 성공!");
    return newAccessToken;
  } catch (error: any) {
    console.error("토큰 재발급 실패:", error);
    // 오류 발생 시 토큰과 유저 정보 삭제
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
    throw error;
  }
};

api.interceptors.request.use(
    async (config) => {
      const token = localStorage.getItem("accessToken");

      if (token) {
        // 요청 전에 토큰 만료 여부 확인
        if (isTokenExpired(token)) {
          console.log("토큰이 만료되었거나 곧 만료됩니다. 갱신 시도 중...");

          // 이미 토큰 갱신 중이면 현재 요청을 대기열에 추가
          if (isRefreshing) {
            console.log("이미 토큰 갱신 중... 요청을 대기열에 추가합니다.");

            return new Promise((resolve) => {
              addSubscriber((newToken: string) => {
                config.headers.Authorization = `Bearer ${newToken}`;
                resolve(config);
              });
            });
          }

          // 토큰 갱신 프로세스 시작
          isRefreshing = true;

          try {
            const newToken = await refreshAccessToken();
            config.headers.Authorization = `Bearer ${newToken}`;

            // 토큰 갱신 완료, 대기 중인 요청 처리
            console.log(
                `토큰 갱신 완료. 대기 중인 요청 ${refreshSubscribers.length}개 처리 중...`
            );
            onRefreshed(newToken);
            isRefreshing = false;
          } catch (error) {
            console.error("사전 토큰 갱신 실패:", error);
            isRefreshing = false;
            onRefreshError(error);

            // 현재 URL이 로그인 페이지가 아닌 경우에만 리다이렉트
            if (!window.location.href.includes("/login")) {
              alert("세션이 만료되었습니다. 다시 로그인해주세요.");
              window.location.href = "/login";
            }

            throw error;
          }
        } else {
          config.headers.Authorization = `Bearer ${token}`;
        }
      }

      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response) => {
      return response;
    },
    async (error) => {
      const originalRequest = error.config;

      // 이미 재시도한 요청이면 더 이상 시도하지 않음
      if (originalRequest._retry) {
        return Promise.reject(error);
      }

      // 토큰 재발급 엔드포인트나 로그인 엔드포인트에서 실패한 경우에는 재시도하지 않음
      if (
          originalRequest.url?.includes("/reissue-token") ||
          originalRequest.url?.includes("/login")
      ) {
        return Promise.reject(error);
      }

      // 403 오류 (권한 부족) 또는 401 오류 (인증 실패)인 경우
      if (error.response?.status === 403 || error.response?.status === 401) {
        originalRequest._retry = true;

        // 이미 토큰 갱신 중이면 현재 요청을 대기열에 추가
        if (isRefreshing) {
          console.log("이미 토큰 갱신 중... 요청을 대기열에 추가합니다.");
          return new Promise((resolve, reject) => {
            addSubscriber((token: string) => {
              if (token) {
                originalRequest.headers.Authorization = `Bearer ${token}`;
                resolve(api(originalRequest));
              } else {
                reject(error);
              }
            });
          });
        }

        // 토큰 갱신 프로세스 시작
        isRefreshing = true;
        console.log(
            `${error.response?.status} 오류 발생. 토큰 갱신 프로세스 시작`
        );

        try {
          const newAccessToken = await refreshAccessToken();
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

          // 토큰 갱신이 성공하면 대기 중인 요청들을 처리
          console.log(
              `토큰 갱신 완료. 대기 중인 요청 ${refreshSubscribers.length}개 처리 중...`
          );
          onRefreshed(newAccessToken);
          isRefreshing = false;

          return api(originalRequest);
        } catch (refreshError) {
          // 토큰 갱신에 실패하면 대기 중인 요청들을 모두 에러로 처리
          console.error("토큰 갱신 실패. 대기 중인 요청 취소");
          isRefreshing = false;
          onRefreshError(refreshError);

          // 현재 URL이 로그인 페이지가 아닌 경우에만 리다이렉트
          if (!window.location.href.includes("/login")) {
            // 사용자에게 세션 만료 알림
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");

            // 로그인 페이지로 리다이렉트
            window.location.href = "/login";
          }

          return Promise.reject(refreshError);
        }
      }

      return Promise.reject(error);
    }
);

export { refreshAccessToken };
export default api;