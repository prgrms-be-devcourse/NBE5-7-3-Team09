package ninegle.Readio.mypage.dto.request

/**
 * 사용자 정보 업데이트 요청 DTO
 */
data class UserUpdateRequestDto(
	val nickname: String? = null,
	val phoneNumber: String? = null
)