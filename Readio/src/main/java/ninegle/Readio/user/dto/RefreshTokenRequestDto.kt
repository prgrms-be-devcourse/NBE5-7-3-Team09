package ninegle.Readio.user.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank


data class RefreshTokenRequestDto(
	@field:JsonProperty("refreshToken") @param:JsonProperty("refreshToken")
    @field: NotBlank(message = "refreshToken은 필수 입력값입나다.")
    val refreshToken: String
)

