export const validateEmail = (
  email: string
): { isValid: boolean; message: string } => {
  if (!email.trim()) {
    return { isValid: false, message: "이메일을 입력해주세요." };
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return { isValid: false, message: "유효한 이메일 형식을 입력해주세요." };
  }
  return { isValid: true, message: "" };
};

export const validateAddress = (
  address: string
): { isValid: boolean; message: string } => {
  if (!address.trim()) {
    return { isValid: false, message: "주소를 입력해주세요." };
  }

  if (address.trim().length < 5) {
    return { isValid: false, message: "주소는 최소 5자 이상이어야 합니다." };
  }

  return { isValid: true, message: "" };
};

export const validateZipCode = (
  zipCode: string
): { isValid: boolean; message: string } => {
  if (!zipCode.trim()) {
    return { isValid: false, message: "우편번호를 입력해주세요." };
  }

  const zipCodeRegex = /^\d{5}$/;
  if (!zipCodeRegex.test(zipCode)) {
    return { isValid: false, message: "우편번호는 5자리 숫자여야 합니다." };
  }

  return { isValid: true, message: "" };
};

export const validatePassword = (
  password: string
): { isValid: boolean; message: string } => {
  if (!password) {
    return { isValid: false, message: "비밀번호를 입력해주세요." };
  }

  if (password.length < 8) {
    return { isValid: false, message: "비밀번호는 8자 이상이어야 합니다." };
  }

  if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
    return {
      isValid: false,
      message: "비밀번호는 특수문자를 포함해야 합니다.",
    };
  }

  if (!/[0-9]/.test(password)) {
    return { isValid: false, message: "비밀번호는 숫자를 포함해야 합니다." };
  }

  if (!/[A-Z]/.test(password)) {
    return { isValid: false, message: "비밀번호는 대문자를 포함해야 합니다." };
  }

  return { isValid: true, message: "" };
};

export const validateConfirmPassword = (
  password: string,
  confirmPassword: string
): { isValid: boolean; message: string } => {
  if (!confirmPassword) {
    return { isValid: false, message: "비밀번호 확인을 입력해주세요." };
  }

  if (password !== confirmPassword) {
    return { isValid: false, message: "비밀번호가 일치하지 않습니다." };
  }

  return { isValid: true, message: "" };
};

export const validateAdminCode = (
  adminCode: string
): { isValid: boolean; message: string } => {
  if (!adminCode.trim()) {
    return { isValid: false, message: "관리자 코드를 입력해주세요." };
  }

  return { isValid: true, message: "" };
};

export const validateCheckoutForm = (
  email: string,
  address: string,
  zipCode: string
): {
  isValid: boolean;
  errors: { email?: string; address?: string; zipCode?: string };
} => {
  const emailValidation = validateEmail(email);
  const addressValidation = validateAddress(address);
  const zipCodeValidation = validateZipCode(zipCode);

  const isValid =
    emailValidation.isValid &&
    addressValidation.isValid &&
    zipCodeValidation.isValid;

  const errors: { email?: string; address?: string; zipCode?: string } = {};

  if (!emailValidation.isValid) {
    errors.email = emailValidation.message;
  }

  if (!addressValidation.isValid) {
    errors.address = addressValidation.message;
  }

  if (!zipCodeValidation.isValid) {
    errors.zipCode = zipCodeValidation.message;
  }
  return { isValid, errors };
};

// 기존 코드는 그대로 유지하고, 새로운 함수만 추가합니다

// 닉네임 유효성 검사
export const validateNickname = (
  nickname: string
): { isValid: boolean; message: string } => {
  if (!nickname.trim()) {
    return { isValid: false, message: "닉네임을 입력해주세요." };
  }

  if (nickname.trim().length < 2) {
    return { isValid: false, message: "닉네임은 2자 이상이어야 합니다." };
  }

  if (nickname.trim().length > 20) {
    return { isValid: false, message: "닉네임은 20자 이하여야 합니다." };
  }

  return { isValid: true, message: "" };
};

// 핸드폰 번호 유효성 검사
export const validatePhoneNumber = (
  phoneNumber: string
): { isValid: boolean; message: string } => {
  if (!phoneNumber.trim()) {
    return { isValid: false, message: "핸드폰 번호를 입력해주세요." };
  }

  // 숫자와 하이픈만 허용
  const phoneRegex = /^[0-9-]+$/;
  if (!phoneRegex.test(phoneNumber)) {
    return { isValid: false, message: "유효한 핸드폰 번호를 입력해주세요." };
  }

  // 형식을 맞춘 상태에서 길이 체크 (010-1234-5678 형식이면 13자)
  const cleanedNumber = phoneNumber.replace(/-/g, "");
  if (cleanedNumber.length !== 11) {
    return { isValid: false, message: "핸드폰 번호는 11자리여야 합니다." };
  }

  // 한국 핸드폰 번호 패턴 체크 (01X로 시작)
  if (!cleanedNumber.startsWith("01")) {
    return { isValid: false, message: "핸드폰 번호는 01로 시작해야 합니다." };
  }

  return { isValid: true, message: "" };
};

// 핸드폰 번호 자동 포맷팅 함수
export const formatPhoneNumber = (value: string): string => {
  const numbers = value.replace(/[^\d]/g, "");

  if (numbers.length <= 3) {
    return numbers;
  } else if (numbers.length <= 7) {
    return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
  } else {
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(
      7,
      11
    )}`;
  }
};

// 회원가입 폼 유효성 검사 함수 업데이트
export const validateSignupForm = (
  nickname: string,
  email: string,
  phone_number: string,
  password: string,
  confirmPassword: string
): {
  isValid: boolean;
  errors: {
    name?: string;
    nickname?: string;
    email?: string;
    phone_number?: string;
    password?: string;
    confirmPassword?: string;
  };
} => {
  const nicknameValidation = validateNickname(nickname);
  const emailValidation = validateEmail(email);
  const phoneNumberValidation = validatePhoneNumber(phone_number);
  const passwordValidation = validatePassword(password);
  const confirmPasswordValidation = validateConfirmPassword(
    password,
    confirmPassword
  );

  const isValid =
    nicknameValidation.isValid &&
    emailValidation.isValid &&
    phoneNumberValidation.isValid &&
    passwordValidation.isValid &&
    confirmPasswordValidation.isValid;

  const errors: {
    name?: string;
    nickname?: string;
    email?: string;
    phone_number?: string;
    password?: string;
    confirmPassword?: string;
  } = {};

  if (!nicknameValidation.isValid) {
    errors.nickname = nicknameValidation.message;
  }

  if (!emailValidation.isValid) {
    errors.email = emailValidation.message;
  }

  if (!phoneNumberValidation.isValid) {
    errors.phone_number = phoneNumberValidation.message;
  }

  if (!passwordValidation.isValid) {
    errors.password = passwordValidation.message;
  }

  if (!confirmPasswordValidation.isValid) {
    errors.confirmPassword = confirmPasswordValidation.message;
  }

  return { isValid, errors };
};
