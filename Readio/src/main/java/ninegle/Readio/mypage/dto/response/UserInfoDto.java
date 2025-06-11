package ninegle.Readio.mypage.dto.response;

public record UserInfoDto(
	String email,
	String nickname,
	String phoneNumber,
	long point) {
}