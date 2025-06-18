package ninegle.Readio.user.dto

import jakarta.validation.constraints.NotBlank

data class DeleteUserRequestDto(

	@field:NotBlank(message = "refreshToken은 필수입니다.")
	val refreshToken:String,

	@field:NotBlank(message = "email은 필수입니다.")
	val email: String,

	@field:NotBlank(message = "password는 필수입니다.")
	 val password: String
)
