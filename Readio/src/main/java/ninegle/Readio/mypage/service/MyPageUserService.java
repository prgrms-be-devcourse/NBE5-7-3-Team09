package ninegle.Readio.mypage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.user.domain.User;
import ninegle.Readio.user.repository.UserRepository;
import ninegle.Readio.user.service.UserContextService;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.mypage.dto.request.UserUpdateRequestDto;
import ninegle.Readio.mypage.dto.response.UserInfoDto;
import ninegle.Readio.mypage.mapper.MyPageUserMapper;

@Service
@RequiredArgsConstructor
public class MyPageUserService {

	private final UserRepository userRepository;
	private final UserContextService userContextService;

	// 읽기 전용 트랜잭션
	@Transactional(readOnly = true)
	public UserInfoDto getUserInfo() {
		Long userId = userContextService.getCurrentUserId();

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		return MyPageUserMapper.toUserInfoDto(user);
	}

	// 데이터 수정하는 메서드
	@Transactional
	public UserInfoDto updateUserInfo(UserUpdateRequestDto dto) {
		Long userId = userContextService.getCurrentUserId();

		// 닉네임, 전화번호 둘 다 비어있는 경우
		if ((dto.getNickname() == null || dto.getNickname().isBlank()) &&
			(dto.getPhoneNumber() == null || dto.getPhoneNumber().isBlank())) {
			throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD);
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 닉네임 업데이트 로직
		if (dto.getNickname() != null) {
			validateNickname(dto.getNickname());
			user.updateNickname(dto.getNickname());
		}

		// 전화번호 업데이트 로직
		if (dto.getPhoneNumber() != null) {
			validatePhoneNumber(dto.getPhoneNumber());
			user.updatePhoneNumber(dto.getPhoneNumber());
		}

		return MyPageUserMapper.toUserInfoDto(userRepository.save(user));
	}

	// 닉네임 검증 메서드
	private void validateNickname(String nickname) {
		if (nickname.isBlank()) {
			throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD);
		}
		if (nickname.length() > 50) {
			throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
		}
	}

	// 핸드폰번호 검증 메서드
	private void validatePhoneNumber(String phoneNumber) {
		if (phoneNumber.isBlank()) {
			throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD);
		}

		String phoneRegex = "^010-\\d{4}-\\d{4}$";
		if (!phoneNumber.matches(phoneRegex)) {
			throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
		}
	}
}