package ninegle.Readio.book.dto.preferencedto

import lombok.Builder
import java.math.BigDecimal

data class PreferenceResponseDto(
    val id: Long,
    val name: String,
    val image: String,
    val rating: BigDecimal
)
