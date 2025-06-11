package ninegle.Readio.book.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ninegle.Readio.book.dto.BookRequestDto;
import ninegle.Readio.book.dto.BookResponseDto;
import ninegle.Readio.book.service.BookService;
import ninegle.Readio.global.unit.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/books")
public class AdminBookController {
	private final BookService bookService;

	@PostMapping
	public ResponseEntity<BaseResponse<Void>> save(@ModelAttribute @Valid BookRequestDto request) throws IOException {
		bookService.save(request);
		return BaseResponse.okOnlyStatus(HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BaseResponse<BookResponseDto>> updateBook(@PathVariable Long id,
		@ModelAttribute BookRequestDto request) {
		BookResponseDto response = bookService.updateBook(id, request);
		return BaseResponse.ok("책 수정이 정상적으로 수행되었습니다.", response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<BaseResponse<Void>> deleteBook(@PathVariable Long id) {
		bookService.deleteBook(id);
		return BaseResponse.okOnlyStatus(HttpStatus.OK);
	}

}