package ninegle.Readio.mypage.dto.response

/**
 * 사용자 정보 응답 DTO
 */
data class UserInfoDto(
	val email: String,
	val nickname: String?,
	val phoneNumber: String?,
	val point: Long
)