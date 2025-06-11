package ninegle.Readio.mypage.mapper;

import ninegle.Readio.user.domain.User;
import ninegle.Readio.mypage.dto.response.UserInfoDto;

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