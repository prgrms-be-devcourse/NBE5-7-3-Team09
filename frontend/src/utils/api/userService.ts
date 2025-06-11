// src/utils/api/userService.ts

import api from "./axiosConfig";
import { UserProfile, ProfileUpdateRequest, Subscription } from "@/types/user";

/**
 * 회원 정보를 조회하는 함수
 */
export const getUserProfile = async (): Promise<UserProfile> => {
  try {
    const response = await api.get("/user/my");

    if (response.data.status === 200) {
      return response.data.data;
    }

    throw new Error(response.data.message || "회원 정보 조회에 실패했습니다.");
  } catch (error) {
    console.error("회원 정보 조회 오류:", error);
    throw error;
  }
};

/**
 * 포인트 정보를 조회하는 함수
 */
export const getUserPoints = async (): Promise<number> => {
  try {
    const response = await api.get("/user/my/points");

    if (response.data.status === 200) {
      return response.data.data.currentPoint;
    }

    throw new Error(response.data.message || "포인트 조회에 실패했습니다.");
  } catch (error) {
    console.error("포인트 조회 오류:", error);
    throw error;
  }
};

/**
 * 회원 정보를 업데이트하는 함수
 */
export const updateUserProfile = async (
  profileData: ProfileUpdateRequest
): Promise<UserProfile> => {
  try {
    const response = await api.post("/user/my", profileData);

    if (response.data.status === 200) {
      return response.data.data;
    }

    throw new Error(response.data.message || "회원 정보 수정에 실패했습니다.");
  } catch (error) {
    console.error("회원 정보 수정 오류:", error);
    throw error;
  }
};

/**
 * 사용자 구독 정보를 가져오는 함수
 */
export const getUserSubscription = async (): Promise<Subscription | null> => {
  try {
    const response = await api.get("/subscriptions");

    if (response.data.status === 200) {
      // 구독 정보가 없는 경우
      if (
        response.data.message === "존재하는 구독이 없습니다." ||
        !response.data.data
      ) {
        return {
          isActive: false,
        };
      }

      // 구독 정보가 있는 경우
      const subscriptionData = response.data.data;

      // 만료일이 오늘 이후인지 체크
      const today = new Date();
      const expDate = new Date(subscriptionData.expDate);
      const isStillActive = expDate > today;

      return {
        isActive: subscriptionData.active, // API에서 주는 active 값 사용
        // 만료일이 오늘 이후라면 여전히 서비스 이용 가능
        stillValid: isStillActive,
        plan: "Premium", // API에 플랜 이름이 없어서 일단 "Premium"으로 고정
        startDate: subscriptionData.subDate,
        endDate: subscriptionData.expDate,
        price: 14900, // API에 가격 정보가 없어서 일단 고정값으로 설정
        userId: subscriptionData.userId,
      };
    }

    throw new Error(response.data.message || "구독 정보 조회에 실패했습니다.");
  } catch (error) {
    console.error("구독 정보 조회 오류:", error);
    // 오류가 발생해도 기본 객체 반환
    return {
      isActive: false,
    };
  }
};

/**
 * 구독을 시작하는 함수
 */
export const startSubscription = async (): Promise<{
  success: boolean;
  message: string;
}> => {
  try {
    const response = await api.post("/subscriptions");

    return {
      success: response.data.status === 200,
      message: response.data.message || "구독 결제에 성공하였습니다.",
    };
  } catch (error: any) {
    console.error("구독 시작 오류:", error);

    // 오류 응답이 있는 경우
    if (error.response && error.response.data) {
      return {
        success: false,
        message: error.response.data.message || "구독 결제에 실패했습니다.",
      };
    }

    return {
      success: false,
      message: "구독 결제 중 오류가 발생했습니다.",
    };
  }
};

/**
 * 구독을 취소하는 함수
 */
export const cancelSubscription = async (
  subscriptionId: number
): Promise<{ success: boolean; message: string }> => {
  try {
    const response = await api.delete(`/subscriptions/${subscriptionId}`);

    return {
      success: response.data.status === 200,
      message: response.data.message || "구독이 취소되었습니다.",
    };
  } catch (error: any) {
    console.error("구독 취소 오류:", error);

    // 오류 응답이 있는 경우
    if (error.response && error.response.data) {
      return {
        success: false,
        message: error.response.data.message || "구독 취소에 실패했습니다.",
      };
    }

    return {
      success: false,
      message: "구독 취소 중 오류가 발생했습니다.",
    };
  }
};
