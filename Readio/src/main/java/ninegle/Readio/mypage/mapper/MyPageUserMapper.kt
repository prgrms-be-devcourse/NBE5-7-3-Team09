package ninegle.Readio.mypage.mapper

import ninegle.Readio.mypage.dto.response.UserInfoDto
import ninegle.Readio.user.domain.User
object MyPageUserMapper {
	fun toUserInfoDto(user: User): UserInfoDto {
		return UserInfoDto(
			email = user.email,
			nickname = user.nickname,
			phoneNumber = user.phoneNumber,
			point = user.point
		)
	}
}