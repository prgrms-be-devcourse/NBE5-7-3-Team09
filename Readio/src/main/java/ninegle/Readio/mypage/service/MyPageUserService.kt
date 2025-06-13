package ninegle.Readio.mypage.service

import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.mypage.dto.request.UserUpdateRequestDto
import ninegle.Readio.mypage.dto.response.UserInfoDto
import ninegle.Readio.mypage.mapper.MyPageUserMapper
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.UserContextService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 마이페이지 사용자 정보 관리 서비스
 */
@Service
class MyPageUserService(
	private val userRepository: UserRepository,
	private val userContextService: UserContextService
) {

	companion object {
		private const val MAX_NICKNAME_LENGTH = 50
		private val PHONE_NUMBER_REGEX = Regex("^010-\\d{4}-\\d{4}$")
	}

	//사용자 정보 조회
	@Transactional(readOnly = true)
	fun getUserInfo(): UserInfoDto {
		val userId = userContextService.getCurrentUserId()

		val user = userRepository.findById(userId).orElseThrow {
			BusinessException(ErrorCode.USER_NOT_FOUND)
		}

		return MyPageUserMapper.toUserInfoDto(user)
	}

	//사용자 정보 업데이트
	@Transactional
	fun updateUserInfo(dto: UserUpdateRequestDto): UserInfoDto {
		val userId = userContextService.getCurrentUserId()

		// 닉네임, 전화번호 둘 다 비어있는 경우 예외 처리
		if (dto.nickname.isNullOrBlank() && dto.phoneNumber.isNullOrBlank()) {
			throw BusinessException(ErrorCode.MISSING_REQUIRED_FIELD)
		}

		val user = userRepository.findById(userId).orElseThrow {
			BusinessException(ErrorCode.USER_NOT_FOUND)
		}

		// 닉네임 업데이트 로직
		dto.nickname?.let { nickname ->
			validateNickname(nickname)
			user.updateNickname(nickname)
		}

		// 전화번호 업데이트 로직
		dto.phoneNumber?.let { phoneNumber ->
			validatePhoneNumber(phoneNumber)
			user.updatePhoneNumber(phoneNumber)
		}

		return MyPageUserMapper.toUserInfoDto(user)
	}

	//닉네임 유효성 검증 메서드
	private fun validateNickname(nickname: String) {
		if (nickname.isBlank()) {
			throw BusinessException(ErrorCode.MISSING_REQUIRED_FIELD)
		}
		if (nickname.length > MAX_NICKNAME_LENGTH) {
			throw BusinessException(ErrorCode.INVALID_REQUEST_DATA)
		}
	}

	//핸드폰번호 유효성 검증 메서드
	private fun validatePhoneNumber(phoneNumber: String) {
		if (phoneNumber.isBlank()) {
			throw BusinessException(ErrorCode.MISSING_REQUIRED_FIELD)
		}

		if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
			throw BusinessException(ErrorCode.INVALID_REQUEST_DATA)
		}
	}
}