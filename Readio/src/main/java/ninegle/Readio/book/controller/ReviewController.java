package ninegle.Readio.book.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ninegle.Readio.book.dto.reviewdto.ReviewListResponseDto;
import ninegle.Readio.book.dto.reviewdto.ReviewRequestDto;
import ninegle.Readio.book.service.ReviewService;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.global.unit.BaseResponse;
import ninegle.Readio.user.service.UserContextService;

/**
 * Readio - ReviewController
 * create date:    25. 5. 16.
 * last update:    25. 5. 16.
 * author:  gigol
 * purpose: 
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class ReviewController {

	private final ReviewService reviewService;
	private final UserContextService userContextService;

	@PostMapping("/{book_id}/reviews")
	public ResponseEntity<BaseResponse<Void>> save(@RequestBody @Valid ReviewRequestDto review,
		@PathVariable("book_id") Long bookId) {
		Long currentUserId = userContextService.getCurrentUserId();
		reviewService.save(currentUserId,review, bookId);
		return BaseResponse.okOnlyStatus(HttpStatus.CREATED);
	}

	@DeleteMapping("/{book_id}/reviews/{review_id}")
	public ResponseEntity<BaseResponse<Void>> delete(@PathVariable("review_id") Long reviewId) {
		reviewService.delete(reviewId);
		return BaseResponse.okOnlyStatus(HttpStatus.NO_CONTENT);
	}

	@PutMapping("/{book_id}/reviews/{review_id}")
	public ResponseEntity<BaseResponse<Void>> update(@RequestBody @Valid ReviewRequestDto review,
		@PathVariable("review_id") Long reviewId) {
		reviewService.update(review, reviewId);
		return BaseResponse.okOnlyStatus(HttpStatus.OK);
	}

	@GetMapping("/{book_id}/reviews")
	public ResponseEntity<BaseResponse<ReviewListResponseDto>> getReviews(@PathVariable("book_id") Long bookId
		, @RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "3") int size) {
		if (page < 1 || size < 1 || size > 50) {
			throw new BusinessException(ErrorCode.INVALID_PAGINATION_PARAMETER);
		}
		ReviewListResponseDto reviewList = reviewService.getReviewList(bookId, page, size);
		return BaseResponse.ok("조회가 성공적으로 수행되었습니다.", reviewList, HttpStatus.OK);
	}

}
