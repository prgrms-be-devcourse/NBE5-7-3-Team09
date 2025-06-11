package ninegle.Readio.user.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;

import ninegle.Readio.user.domain.User;
import ninegle.Readio.user.dto.Delete;
import ninegle.Readio.user.dto.DeleteUserRequestDto;
import ninegle.Readio.user.dto.SingUpRequestDto;

public class UserMapper {

	public static User toUser(SingUpRequestDto dto, PasswordEncoder passwordEncoder) {
		return User.builder()
			.email(dto.email())
			.password(passwordEncoder.encode(dto.password()))  // 암호화
			.nickname(dto.nickname())
			.phoneNumber(dto.phoneNumber())
			.build();
	}

	public static Delete toDelete(DeleteUserRequestDto deleteUserRequestDto) {
		return Delete.builder().email(deleteUserRequestDto.email())
			.password(deleteUserRequestDto.password())
			.refreshToken(deleteUserRequestDto.refreshToken())
			.build();
	}
}
