package ninegle.Readio.book.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import ninegle.Readio.book.dto.BookRequestDto
import ninegle.Readio.book.dto.BookResponseDto
import ninegle.Readio.book.service.BookService
import ninegle.Readio.global.unit.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.IOException

val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/admin/books")
class AdminBookController(
    private val bookService: BookService
) {

    @PostMapping
    @Throws(IOException::class)
    fun save(@ModelAttribute @Valid request: BookRequestDto): ResponseEntity<BaseResponse<Void>> {
        bookService.save(request)
        return BaseResponse.okOnlyStatus(HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @ModelAttribute request: BookRequestDto
    ): ResponseEntity<BaseResponse<BookResponseDto>> {
        log.info { request.toString() }
        val response = bookService.updateBook(id, request)
        return BaseResponse.ok("책 수정이 정상적으로 수행되었습니다.", response, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<BaseResponse<Void>> {
        bookService.deleteBook(id)
        return BaseResponse.okOnlyStatus(HttpStatus.OK)
    }
}