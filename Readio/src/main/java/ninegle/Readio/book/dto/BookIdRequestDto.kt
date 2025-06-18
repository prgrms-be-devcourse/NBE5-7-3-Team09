package ninegle.Readio.book.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull


data class BookIdRequestDto (
    @field: NotNull(message = "도서 ID는 필수입니다.")
    @field: Min(
        value = 1,
        message = "도서 ID는 1 이상의 값이어야 합니다."
    )
    val id : Long?
)
