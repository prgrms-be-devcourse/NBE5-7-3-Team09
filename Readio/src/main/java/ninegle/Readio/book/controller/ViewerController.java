package ninegle.Readio.book.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.book.dto.viewer.ViewerResponseDto;
import ninegle.Readio.book.service.BookService;
import ninegle.Readio.global.unit.BaseResponse;

@RestController
@RequestMapping("/viewer/books")
@RequiredArgsConstructor
public class ViewerController {

	private final BookService bookService;

	@GetMapping("/{bookId}")
	public ResponseEntity<BaseResponse<ViewerResponseDto>> getBookDetail(@PathVariable Long bookId) {
		ViewerResponseDto response = bookService.getViewerBook(bookId);
		return BaseResponse.ok("요청에 성공했습니다.", response, HttpStatus.OK);
	}

}
