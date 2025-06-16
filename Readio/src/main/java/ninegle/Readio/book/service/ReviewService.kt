package ninegle.Readio.book.service

import ninegle.Readio.book.domain.Review
import ninegle.Readio.book.dto.reviewdto.ReviewListResponseDto
import ninegle.Readio.book.dto.reviewdto.ReviewRequestDto
import ninegle.Readio.book.mapper.ReviewMapper
import ninegle.Readio.book.repository.BookRepository
import ninegle.Readio.book.repository.BookSearchRepository
import ninegle.Readio.book.repository.ReviewRepository
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.user.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ReviewService(
    val userService: UserService,
    val reviewMapper: ReviewMapper,
    val reviewRepository: ReviewRepository,
    val bookSearchRepository: BookSearchRepository,
    val bookService: BookService,
    val bookRepository: BookRepository
) {


    fun getReviewById(id: Long): Review {
        return reviewRepository.findById(id).orElseThrow { BusinessException(ErrorCode.REVIEW_NOT_FOUND) }
    }

    private fun updateRatingInBookSearch(bookId: Long) {
        var rating = reviewRepository.findAverageRatingByBook(bookId)
        if (rating == null) {
            rating = BigDecimal.ZERO
        }

        val findBook = bookRepository.findById(bookId)
            .orElseThrow { BusinessException(ErrorCode.BOOK_NOT_FOUND) }

        val bookSearch = bookSearchRepository.findById(bookId)
            .orElseThrow { BusinessException(ErrorCode.BOOK_NOT_FOUND) }

        findBook.updateRating(rating!!)
        bookSearch.updateRating(rating)

        bookSearchRepository.save(bookSearch)
    }

    // Review Create
    @Transactional
    fun save(userId: Long?, reviewRequestDto: ReviewRequestDto, book_id: Long) {
        val user = userService.getById(userId)
        val book = bookService.getBookById(book_id)
        reviewRepository.save(reviewMapper.toEntity(reviewRequestDto, user, book))

        reviewRepository.flush()

        updateRatingInBookSearch(book_id)
    }

    // Review Delete
    @Transactional
    fun delete(reviewId: Long) {
        val review = getReviewById(reviewId)

        reviewRepository.delete(review)
        reviewRepository.flush()

        updateRatingInBookSearch(review.book.id!!)
    }

    // Review Update
    @Transactional
    fun update(reviewRequestDto: ReviewRequestDto, reviewId: Long) {
        val review = getReviewById(reviewId)
        reviewRepository.save(review.update(reviewRequestDto))
        reviewRepository.flush()

        updateRatingInBookSearch(review.book.id!!)
    }
//  매퍼 문제 해결후 주석 해제
//    fun getReviewList(bookId: Long, page: Int, size: Int): ReviewListResponseDto {
//        val book = bookService.getBookById(bookId)
//        val pageable: Pageable = PageRequest.of(page - 1, size)
//        val count = reviewRepository.countByBook(book)
//        val average = reviewRepository.findAverageRatingByBook(book.id!!)
//
//        val reviews = reviewRepository.findReviewsByBook(book, pageable)!!.content
//        val reviewList = reviewMapper.toResponseDto(reviews)
//
//        val paginationDto = reviewMapper.toPaginationDto(count, page, size)
//        val summaryDto = reviewMapper.toSummaryDto(count, average!!)
//
//        return reviewMapper.toReviewListResponseDto(
//            reviewList, paginationDto,
//            summaryDto
//        )
//    }
}
