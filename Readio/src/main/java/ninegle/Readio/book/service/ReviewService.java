package ninegle.Readio.book.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ninegle.Readio.book.domain.Book;
import ninegle.Readio.book.domain.BookSearch;
import ninegle.Readio.book.domain.Review;
import ninegle.Readio.book.dto.PaginationDto;
import ninegle.Readio.book.dto.reviewdto.ReviewListResponseDto;
import ninegle.Readio.book.dto.reviewdto.ReviewRequestDto;
import ninegle.Readio.book.dto.reviewdto.ReviewResponseDto;
import ninegle.Readio.book.dto.reviewdto.ReviewSummaryDto;
import ninegle.Readio.book.mapper.ReviewMapper;
import ninegle.Readio.book.repository.BookRepository;
import ninegle.Readio.book.repository.BookSearchRepository;
import ninegle.Readio.book.repository.ReviewRepository;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.global.unit.BaseResponse;
import ninegle.Readio.user.domain.User;
import ninegle.Readio.user.service.UserContextService;
import ninegle.Readio.user.service.UserService;

/**
 * Readio - ReviewService
 * create date:    25. 5. 16.
 * last update:    25. 5. 16.
 * author:  gigol
 * purpose: 
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

	private final BookService bookService;
	private final ReviewRepository reviewRepository;
	private final ReviewMapper reviewMapper;
	private final UserService userService;
	private final UserContextService userContextService;
	private final BookSearchRepository bookSearchRepository;
	private final BookRepository bookRepository;


	public Review getReviewById(long id) {
		return reviewRepository.findById(id).orElseThrow(
			() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
	}

	private void updateRatingInBookSearch(long bookId) {
		BigDecimal rating = reviewRepository.findAverageRatingByBook(bookId);
		if (rating == null) {
			rating = BigDecimal.ZERO;
		}

		Book findBook = bookRepository.findById(bookId)
			.orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

		BookSearch bookSearch = bookSearchRepository.findById(bookId)
			.orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

		findBook.updateRating(rating);
		bookSearch.updateRating(rating);

		bookSearchRepository.save(bookSearch);
	}

	// Review Create
	@Transactional
	public void save(Long userId,ReviewRequestDto reviewRequestDto, long book_id) {
		User user = userService.getById(userId);
		Book book = bookService.getBookById(book_id);
		reviewRepository.save(reviewMapper.toEntity(reviewRequestDto, user, book));

		reviewRepository.flush();

		updateRatingInBookSearch(book_id);
	}

	// Review Delete
	@Transactional
	public void delete(Long reviewId) {
		Review review = getReviewById(reviewId);

		reviewRepository.delete(review);
		reviewRepository.flush();

		updateRatingInBookSearch(review.getBook().getId());
	}

	// Review Update
	@Transactional
	public void update(ReviewRequestDto reviewRequestDto, Long reviewId) {
		Review review = getReviewById(reviewId);
		reviewRepository.save(reviewMapper.updateEntity(review, reviewRequestDto));
		reviewRepository.flush();

		updateRatingInBookSearch(review.getBook().getId());;
	}

	public ReviewListResponseDto getReviewList(Long bookId, int page, int size) {

		Book book = bookService.getBookById(bookId);
		Pageable pageable = PageRequest.of(page - 1, size);
		long count = reviewRepository.countByBook(book);
		BigDecimal average = reviewRepository.findAverageRatingByBook(book.getId());

		List<Review> reviews = reviewRepository.findReviewsByBook(book, pageable).getContent();
		List<ReviewResponseDto> reviewList = reviewMapper.toResponseDto(reviews);

		PaginationDto paginationDto = reviewMapper.toPaginationDto(count, page, size);
		ReviewSummaryDto summaryDto = reviewMapper.toSummaryDto(count, average);

		return reviewMapper.toReviewListResponseDto(reviewList, paginationDto,
			summaryDto);

	}
}
