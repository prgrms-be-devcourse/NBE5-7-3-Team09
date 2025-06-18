package ninegle.Readio.book.dto.reviewdto

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal


data class ReviewRequestDto(
    @field : DecimalMax(value = "5.0", inclusive = true, message = "별점은 5.0이하 이어야 합니다.")
    @field : DecimalMin(value = "0.0", inclusive = true, message = "별점은 0.0이상 이어야 합니다."
    )
    val rating : BigDecimal,
    @field : NotBlank(message = "리뷰 텍스트는 필수입니다.")
    val text: String
)
