package ninegle.Readio.library.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.library.dto.book.LibraryBookListResponseDto
import ninegle.Readio.library.dto.book.NewLibraryBookRequestDto
import ninegle.Readio.library.service.LibraryBookService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class LibraryBookController (
    private val libraryBookService: LibraryBookService)
{
    
    //라이브러리에 책 추가
    @PostMapping("/library/{libraryId}")
    fun addBook(
        @PathVariable libraryId: @NotNull @Max(5000) Long,
        @RequestBody bookRequestDto: @Valid NewLibraryBookRequestDto): ResponseEntity<BaseResponse<Void>> {
        libraryBookService.newLibraryBook(libraryId, bookRequestDto)
        return BaseResponse.okOnlyStatus(HttpStatus.OK)
    }

    //라이브러리에 책들 불러오기
    @GetMapping("/library/{libraryId}/library-books")
    fun listAllBooks(
        @PathVariable @NotNull @Max(5000) libraryId: Long,
        @RequestParam(defaultValue = "0") @Max(500) @Min(0)  page:Int,
        @RequestParam(defaultValue = "10")  @Max(5000) @Min(1) size:Int): ResponseEntity<BaseResponse<LibraryBookListResponseDto>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val response = libraryBookService.getAllLibraryBooks(libraryId, pageable)
        return BaseResponse.ok("전체 라이브러리 조회 완료", response, HttpStatus.OK)
    }

    //라이브러리에 책 삭제
    @DeleteMapping("/library/{libraryId}/library-books/{bookId}")
    fun deleteBook(
        @PathVariable @NotNull @Max(5000) @Min(1) libraryId: Long,
        @PathVariable @NotNull @Max(5000) @Min(1) bookId: Long): ResponseEntity<BaseResponse<Void>> {
        libraryBookService.deleteLibraryBook(libraryId, bookId)
        return BaseResponse.okOnlyStatus(HttpStatus.OK)
    }
}
