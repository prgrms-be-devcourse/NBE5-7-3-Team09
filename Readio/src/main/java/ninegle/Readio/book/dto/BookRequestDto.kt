package ninegle.Readio.book.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

data class BookRequestDto(
    @field: NotBlank(message = "카테고리를 선택해주세요.")
    val categorySub: String,

    @field: NotBlank(message = "출판사를 입력해주세요.")
    val publisherName: String,

    @field: NotBlank(message = "작가를 입력해주세요.")
    val authorName: String,

    @field: NotBlank(message = "책 제목을 입력해주세요.")
    val name: String,

    val description: String,

    @field: NotNull(message = "이미지 파일은 필수입니다.")
    val image: MultipartFile,

    @field: NotBlank(message = "ISBN을 입력해주세요.")
    val isbn: String,

    val ecn: String,

    @field: NotNull(message = "출판일자를 입력해주세요.")
    val pubDate: LocalDate,

    @field: NotNull(message = "EPUB 파일은 필수입니다.")
    val epubFile: MultipartFile

) {
}
