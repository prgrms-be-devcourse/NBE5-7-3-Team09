package ninegle.Readio.book.repository

import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.Review
import ninegle.Readio.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findReviewsByBook(book: Book, pageable: Pageable): Page<Review>?

    fun countByBook(book: Book): Long

    @Query(value = "SELECT ROUND(AVG(rating), 1) FROM review WHERE book_id = :bookId", nativeQuery = true)
    fun findAverageRatingByBook(@Param("bookId") bookId: Long): BigDecimal?

    fun findAllByUserId(userId: Long): List<Review>?

    fun existsByUserAndBook(user: User, book: Book): Boolean
}
