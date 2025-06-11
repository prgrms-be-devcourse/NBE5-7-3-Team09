package ninegle.Readio.mypage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.mypage.dto.response.PointResponseDto;
import ninegle.Readio.user.domain.User;
import ninegle.Readio.user.repository.UserRepository;
import ninegle.Readio.user.service.UserContextService;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;

@Service
@RequiredArgsConstructor
public class PointService {

	private final UserContextService userContextService;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public PointResponseDto getUserPoints() {
		Long userId = userContextService.getCurrentUserId();

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		return new PointResponseDto(user.getPoint());
	}
}