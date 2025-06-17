package ninegle.Readio.mypage.mapper;

import ninegle.Readio.mypage.dto.response.UserInfoDto;
import ninegle.Readio.user.domain.User;

public class MyPageUserMapper {

	public static UserInfoDto toUserInfoDto(User user) {
		return new UserInfoDto(
			user.getEmail(),
			user.getNickname(),
			user.getPhoneNumber(),
			user.getPoint()
		);
	}
}