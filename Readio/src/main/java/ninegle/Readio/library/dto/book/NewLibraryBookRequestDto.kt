package ninegle.Readio.library.dto.book

import jakarta.validation.constraints.NotBlank


data class NewLibraryBookRequestDto(
    @NotBlank (message = "id값 입력이 되지 않음")
    val bookId: Long)
