package ninegle.Readio.book.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ninegle.Readio.book.domain.Book;
import ninegle.Readio.book.domain.Review;

/**
 * Readio - ReviewRepository
 * create date:    25. 5. 12.
 * last update:    25. 5. 12.
 * author:  gigol
 * purpose: 
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
	Page<Review> findReviewsByBook(Book book, Pageable pageable);

	long countByBook(Book book);

	@Query(value = "SELECT ROUND(AVG(rating), 1) FROM review WHERE book_id = :bookId", nativeQuery = true)
	BigDecimal findAverageRatingByBook(@Param("bookId") Long bookId);

	List<Review> findAllByUserId(Long userId);
}
