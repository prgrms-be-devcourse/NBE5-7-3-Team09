package ninegle.Readio.mypage.mapper;

import org.springframework.stereotype.Component;

import ninegle.Readio.mypage.dto.response.PointResponseDto;
import ninegle.Readio.user.domain.User;

@Component
public class PointMapper {
	public PointResponseDto toDto(User user) {
		return new PointResponseDto(user.getPoint());
	}
}