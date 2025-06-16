package ninegle.Readio.book.repository

import ninegle.Readio.book.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

/**
 * Readio - BookRepository
 * create date:    25. 5. 9.
 * last update:    25. 5. 9.
 * author:  gigol
 * purpose:
 */
@Repository
interface BookRepository : JpaRepository<Book, Long> {
    fun findByIdAndExpiredFalse(id: Long): Book?

    // 소프트 삭제된 엔티티 중 expiredAt이 threshold 이전인 것 조회
    @Query("SELECT b FROM Book b WHERE b.expired= true AND b.expiredAt < :threshold")
    fun findByExpiredTrueAndExpiredAtBefore(@Param("threshold") threshold: LocalDateTime): MutableList<Book>

    @Modifying
    @Query("DELETE FROM Book b WHERE b.expired = true AND b.expiredAt < :threshold")
    fun deleteExpiredBefore(@Param("threshold") threshold: LocalDateTime): Int

    fun existsByIsbn(isbn: String): Boolean
    fun existsByEcn(ecn: String): Boolean

    fun existsByIsbnAndIdNot(isbn: String, id: Long): Boolean
    fun existsByEcnAndIdNot(ecn: String, id: Long): Boolean
}
