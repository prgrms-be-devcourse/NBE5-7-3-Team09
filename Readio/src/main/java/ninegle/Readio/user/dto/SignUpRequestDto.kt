package ninegle.Readio.user.dto

import jakarta.validation.constraints.NotBlank


data class SignUpRequestDto(
	@field: NotBlank(message = "이메일은 필수 입력값입니다.")
	val email: String,

	@field: NotBlank(message = "비밀번호는 필수 입력값입니다.")
	val password:  String,

	@field: NotBlank(message = "닉네임은 필수 입력값입니다.")
	val nickname:  String,

	@field: NotBlank(message = "전화번호는 필수 입력값입니다.")
	val phoneNumber:  String
)
