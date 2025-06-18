package ninegle.Readio.user.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(

	@field: NotBlank(message = "이메일 입력은 필수입니다.")
	val email: String,

	@field: NotBlank(message = "비밀번호 입력은 필수입니다.")
	val password: String

)

