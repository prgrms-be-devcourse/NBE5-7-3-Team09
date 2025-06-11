import React, { useState, useEffect } from "react";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useNavigate } from "react-router-dom";
import {
  validateNickname,
  validateEmail,
  validatePhoneNumber,
  formatPhoneNumber,
  validatePassword,
  validateConfirmPassword,
  validateSignupForm,
} from "../../utils/validation/CheckoutValidation";
import SignupForm from "@/interface/SignupForm";
import { FormErrors } from "@/interface/FormErrors";
import { authService } from "@/utils/api/authService";

export default function SignupPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<SignupForm>({
    nickname: "",
    email: "",
    phoneNumber: "",
    password: "",
    confirmPassword: "",
  });
  const [errors, setErrors] = useState<FormErrors>({});
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [apiError, setApiError] = useState<string>("");
  const [isFormValid, setIsFormValid] = useState<boolean>(false);

  const checkFormValidity = () => {
    const { isValid } = validateSignupForm(
      formData.nickname,
      formData.email,
      formData.phoneNumber,
      formData.password,
      formData.confirmPassword
    );
    setIsFormValid(isValid);
  };

  useEffect(() => {
    checkFormValidity();
  }, [formData]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;

    // 핸드폰 번호 자동 포맷팅 처리
    if (id === "phoneNumber") {
      const formattedValue = formatPhoneNumber(value);
      setFormData((prev) => ({
        ...prev,
        [id]: formattedValue,
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [id]: value,
      }));
    }

    let validation;
    switch (id) {
      case "nickname":
        validation = validateNickname(value);
        break;
      case "email":
        validation = validateEmail(value);
        break;
      case "phoneNumber":
        const formattedValue = formatPhoneNumber(value);
        validation = validatePhoneNumber(formattedValue);
        break;
      case "password":
        validation = validatePassword(value);
        if (formData.confirmPassword) {
          const confirmValidation = validateConfirmPassword(
            value,
            formData.confirmPassword
          );
          setErrors((prev) => ({
            ...prev,
            confirmPassword: confirmValidation.isValid
              ? undefined
              : confirmValidation.message,
          }));
        }
        break;
      case "confirmPassword":
        validation = validateConfirmPassword(formData.password, value);
        break;
      default:
        return;
    }

    setErrors((prev) => ({
      ...prev,
      [id]: validation.isValid ? undefined : validation.message,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setApiError("");

    const { isValid, errors: formErrors } = validateSignupForm(
      formData.nickname,
      formData.email,
      formData.phoneNumber,
      formData.password,
      formData.confirmPassword
    );

    if (!isValid) {
      setErrors(formErrors);
      return;
    }

    setIsLoading(true);

    try {
      console.log(formData);
      const response = await authService.signup({
        email: formData.email,
        password: formData.password,
        nickname: formData.nickname,
        phoneNumber: formData.phoneNumber,
      });

      console.log(response);

      navigate("/login");
    } catch (error: any) {
      if (error.response?.status === 400) {
        if (error.response.data?.errors) {
          setErrors(error.response.data.errors);
        } else if (error.response.data?.message) {
          setApiError(error.response.data.message);
        } else {
          setApiError("입력한 정보를 확인해주세요.");
        }
      } else if (error.response?.status === 409) {
        if (error.response.data?.field === "email") {
          setErrors((prev) => ({
            ...prev,
            email: "이미 사용 중인 이메일입니다.",
          }));
        } else if (error.response.data?.field === "nickname") {
          setErrors((prev) => ({
            ...prev,
            nickname: "이미 사용 중인 닉네임입니다.",
          }));
        } else {
          setApiError("이미 등록된 정보가 있습니다.");
        }
      } else if (error.request) {
        setApiError("서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.");
      } else {
        setApiError("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex justify-center">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>회원가입</CardTitle>
        </CardHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <CardContent className="space-y-3">
            <div className="space-y-2">
              <Label htmlFor="nickname">닉네임</Label>
              <Input
                id="nickname"
                type="text"
                placeholder="사용할 닉네임"
                value={formData.nickname}
                onChange={handleInputChange}
                className={errors.nickname ? "border-red-500" : ""}
              />
              {errors.nickname && (
                <p className="text-sm text-red-500">{errors.nickname}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                type="email"
                placeholder="test@example.com"
                value={formData.email}
                onChange={handleInputChange}
                className={errors.email ? "border-red-500" : ""}
              />
              {errors.email && (
                <p className="text-sm text-red-500">{errors.email}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                type="password"
                value={formData.password}
                onChange={handleInputChange}
                className={errors.password ? "border-red-500" : ""}
                placeholder="대소문자, 숫자, 특수문자 포함 8자 이상"
              />
              {errors.password && (
                <p className="text-sm text-red-500">{errors.password}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword">비밀번호 확인</Label>
              <Input
                id="confirmPassword"
                type="password"
                value={formData.confirmPassword}
                onChange={handleInputChange}
                className={errors.confirmPassword ? "border-red-500" : ""}
              />
              {errors.confirmPassword && (
                <p className="text-sm text-red-500">{errors.confirmPassword}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="phoneNumber">핸드폰 번호</Label>
              <Input
                id="phoneNumber"
                type="tel"
                placeholder="010-1234-5678"
                value={formData.phoneNumber}
                onChange={handleInputChange}
                className={errors.phoneNumber ? "border-red-500" : ""}
              />
              {errors.phoneNumber && (
                <p className="text-sm text-red-500">{errors.phoneNumber}</p>
              )}
            </div>

            {apiError && <div className="text-sm text-red-500">{apiError}</div>}
          </CardContent>
          <CardFooter className="flex flex-col space-y-2">
            <Button
              type="submit"
              className="w-full bg-blue-500 hover:bg-blue-600 disabled:opacity-70 disabled:cursor-not-allowed"
              disabled={isLoading || !isFormValid}
            >
              {isLoading ? "가입 중..." : "회원가입"}
            </Button>
            <Button
              type="button"
              variant="outline"
              className="w-full"
              onClick={() => navigate("/login")}
            >
              로그인으로 돌아가기
            </Button>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
}
