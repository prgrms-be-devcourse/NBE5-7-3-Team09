package ninegle.Readio.book.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ninegle.Readio.book.domain.Book;

/**
 * Readio - BookRepository
 * create date:    25. 5. 9.
 * last update:    25. 5. 9.
 * author:  gigol
 * purpose:
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Optional<Book> findByIdAndExpiredFalse(Long id);

	// 소프트 삭제된 엔티티 중 expiredAt이 threshold 이전인 것 조회
	@Query("SELECT b FROM Book b WHERE b.expired= true AND b.expiredAt < :threshold")
	List<Book> findByExpiredTrueAndExpiredAtBefore(@Param("threshold") LocalDateTime threshold);

	@Modifying
	@Query("DELETE FROM Book b WHERE b.expired = true AND b.expiredAt < :threshold")
	int deleteExpiredBefore(@Param("threshold") LocalDateTime threshold);

	boolean existsByIsbn(String isbn);
	boolean existsByEcn(String ecn);

	boolean existsByIsbnAndIdNot(String isbn, Long id);
	boolean existsByEcnAndIdNot(String ecn, Long id);
}
