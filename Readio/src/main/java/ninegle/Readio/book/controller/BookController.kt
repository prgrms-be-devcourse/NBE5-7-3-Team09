package ninegle.Readio.book.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import lombok.RequiredArgsConstructor
import ninegle.Readio.book.dto.BookResponseDto
import ninegle.Readio.book.dto.booksearch.BookListResponseDto
import ninegle.Readio.book.service.BookService
import ninegle.Readio.global.unit.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * Readio - BookController
 * create date:    25. 5. 8.
 * last update:    25. 5. 8.
 * author:  gigol
 * purpose:
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
class BookController(
    private val bookService: BookService
) {

    @GetMapping("/{id}")
    fun getBookDetail(@PathVariable id: Long): ResponseEntity<BaseResponse<BookResponseDto>> {
        val response = bookService.getBookDetail(id)
        return BaseResponse.ok("정상적으로 조회가 완료되었습니다.", response, HttpStatus.OK)
    }

    @GetMapping
    fun getBooksByCategoryMajor(
        @RequestParam(name = "category_major", defaultValue = "null")
        categoryMajor: String,
        @RequestParam(defaultValue = "1")
        @Min(value = 1, message = "page는 1 이상이어야 합니다.")
        page: Int,
        @RequestParam(defaultValue = "3")
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 50, message = "size는 50 이하이어야 합니다.")
        size: Int
    ): ResponseEntity<BaseResponse<BookListResponseDto>> {
        val response = bookService.getBookByCategory(categoryMajor, page, size)
        return BaseResponse.ok("카테고리별 조회가 정상적으로 수행되었습니다.", response, HttpStatus.OK)
    }

    @GetMapping("/search")
    fun search(
        @RequestParam(name = "keyword")
        keyword: String,
        @RequestParam(defaultValue = "1")
        @Min(value = 1, message = "page는 1 이상이어야 합니다.")
        page: Int,
        @RequestParam(defaultValue = "3")
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 50, message = "size는 50 이하이어야 합니다.")
        size: Int
    ): ResponseEntity<BaseResponse<BookListResponseDto>> {
        val response = bookService.searchBooks(keyword, page, size)
        return BaseResponse.ok("검색 결과입니다.", response, HttpStatus.OK)
    }
}
