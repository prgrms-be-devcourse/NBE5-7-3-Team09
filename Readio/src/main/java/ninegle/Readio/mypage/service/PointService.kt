package ninegle.Readio.mypage.service

import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.mypage.dto.response.PointResponseDto
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.UserContextService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 마이페이지에 있는 포인트 관리 서비스
 */
@Service
class PointService(
	private val userContextService: UserContextService,
	private val userRepository: UserRepository
) {

	@Transactional(readOnly = true)
	fun getUserPoints(): PointResponseDto {
		val userId = userContextService.currentUserId
			?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

		val user = userRepository.findById(userId).orElseThrow {
			BusinessException(ErrorCode.USER_NOT_FOUND)
		}

		return PointResponseDto(currentPoint = user.point)
	}
}