package ninegle.Readio.mypage.mapper

import ninegle.Readio.mypage.dto.response.PointResponseDto
import ninegle.Readio.user.domain.User
import org.springframework.stereotype.Component
@Component
class PointMapper {
    fun toDto(user: User): PointResponseDto {
        return PointResponseDto(
            currentPoint = user.point
        )
    }
}