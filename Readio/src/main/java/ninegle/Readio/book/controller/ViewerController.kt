package ninegle.Readio.book.controller

import lombok.RequiredArgsConstructor
import ninegle.Readio.book.dto.viewer.ViewerResponseDto
import ninegle.Readio.book.service.BookService
import ninegle.Readio.global.unit.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/viewer/books")
@RequiredArgsConstructor
class ViewerController(
    private val bookService: BookService
) {

    @GetMapping("/{bookId}")
    fun getBookDetail(@PathVariable bookId: Long): ResponseEntity<BaseResponse<ViewerResponseDto>> {
        val response = bookService.getViewerBook(bookId)
        return BaseResponse.ok("요청에 성공했습니다.", response, HttpStatus.OK)
    }
}
