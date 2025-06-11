package ninegle.Readio.library.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import ninegle.Readio.global.unit.BaseResponse;
import ninegle.Readio.library.dto.book.LibraryBookListResponseDto;
import ninegle.Readio.library.dto.book.NewLibraryBookRequestDto;
import ninegle.Readio.library.service.LibraryBookService;

@RestController
@RequiredArgsConstructor
@Validated
public class LibraryBookController {

	private final LibraryBookService libraryBookService;

	//라이브러리에 책 추가
	@PostMapping("/library/{libraryId}")
	public ResponseEntity<BaseResponse<Void>> addBook(
		@PathVariable @NotNull @Max(5000) Long libraryId,
		@RequestBody @Valid NewLibraryBookRequestDto bookRequestDto) {
		libraryBookService.newLibraryBook(libraryId, bookRequestDto);
		return BaseResponse.okOnlyStatus(HttpStatus.OK);

	}

	//라이브러리에 책들 불러오기
	@GetMapping("/library/{libraryId}/library-books")
	public ResponseEntity<BaseResponse<LibraryBookListResponseDto>> listAllBooks(
		@PathVariable @NotNull @Max(5000) Long libraryId,
		@RequestParam(defaultValue = "0") @Max(500) @Min(0) int page,
		@RequestParam(defaultValue = "10") @Max(5000) @Min(1) int size) {
		Pageable pageable = PageRequest.of(page, size);
		LibraryBookListResponseDto response = libraryBookService.getAllLibraryBooks(libraryId, pageable);
		return BaseResponse.ok("전체 라이브러리 조회 완료", response, HttpStatus.OK);
	}

	//라이브러리에 책 삭제
	@DeleteMapping("/library/{libraryId}/library-books/{bookId}")
	public ResponseEntity<BaseResponse<Void>> deleteBook(
		@PathVariable @NotNull @Max(5000) @Min(1) Long libraryId,
		@PathVariable @NotNull @Max(5000) @Min(1) Long bookId) {
		libraryBookService.deleteLibraryBook(libraryId, bookId);
		return BaseResponse.okOnlyStatus(HttpStatus.OK);
	}

}
