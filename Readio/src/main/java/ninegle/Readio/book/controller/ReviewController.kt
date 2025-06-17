package ninegle.Readio.book.controller

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import ninegle.Readio.book.dto.reviewdto.ReviewListResponseDto
import ninegle.Readio.book.dto.reviewdto.ReviewRequestDto
import ninegle.Readio.book.service.ReviewService
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.user.service.UserContextService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class ReviewController(
    val userContextService: UserContextService,
    val reviewService: ReviewService
) {

    @PostMapping("/{book_id}/reviews")
    fun save(
        @RequestBody @Valid
        review: ReviewRequestDto,
        @PathVariable("book_id")
        bookId: Long
    ): ResponseEntity<BaseResponse<Void>> {
        val currentUserId = userContextService.currentUserId
        reviewService.save(currentUserId, review, bookId)
        return BaseResponse.okOnlyStatus(HttpStatus.CREATED)
    }

    @DeleteMapping("/{book_id}/reviews/{review_id}")
    fun delete(
        @PathVariable("review_id") reviewId: Long): ResponseEntity<BaseResponse<Void>> {
        reviewService.delete(reviewId)
        return BaseResponse.okOnlyStatus(HttpStatus.NO_CONTENT)
    }

    @PutMapping("/{book_id}/reviews/{review_id}")
    fun update(
        @RequestBody @Valid
        review: ReviewRequestDto,
        @PathVariable("review_id")
        reviewId: Long
    ): ResponseEntity<BaseResponse<Void>> {
        reviewService.update(review, reviewId)
        return BaseResponse.okOnlyStatus(HttpStatus.OK)
    }
// 매퍼 문제 해결후 주석 해제
    @GetMapping("/{book_id}/reviews")
    fun getReviews(
        @PathVariable("book_id") bookId: Long,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "3") size: Int
    ): ResponseEntity<BaseResponse<ReviewListResponseDto>> {
        if (page < 1 || size < 1 || size > 50) {
            throw BusinessException(ErrorCode.INVALID_PAGINATION_PARAMETER)
        }
        val reviewList = reviewService.getReviewList(bookId, page, size)
        return BaseResponse.ok("조회가 성공적으로 수행되었습니다.", reviewList, HttpStatus.OK)
    }
}
