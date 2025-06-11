// src/types/user.ts

export interface UserProfile {
  nickname: string;
  email: string;
  phoneNumber: string;
  point: number;
}

export interface Subscription {
  isActive: boolean;
  stillValid?: boolean; // 만료일이 현재 이후라면 true
  plan?: string;
  startDate?: string;
  endDate?: string;
  price?: number;
  userId?: number;
}

export interface ProfileUpdateRequest {
  nickname?: string;
  phoneNumber?: string;
}

export interface ProfileUpdateResponse {
  success: boolean;
  message: string;
  data?: UserProfile;
}

export interface SubscriptionPlan {
  id: string;
  name: string;
  price: number;
  features: string[];
}
