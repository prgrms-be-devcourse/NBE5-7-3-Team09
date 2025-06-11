import axios from "./axiosConfig";
import { PublisherResponse } from "../../types/publisher";

export const publisherService = {
  /**
   * 출판사 전체 목록을 조회합니다.
   * @returns Promise<Publisher[]> 출판사 목록
   */
  getAllPublishers: async () => {
    try {
      const response = await axios.get<PublisherResponse>("/admin/publishers");
      return response.data.data.publishers;
    } catch (error) {
      console.error("출판사 목록 조회 중 오류 발생:", error);
      throw error;
    }
  },
};

export default publisherService;
