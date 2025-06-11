package ninegle.Readio.book.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import ninegle.Readio.book.domain.BookSearch;

/**
 * Readio - BookSearchRepository
 * create date:    25. 5. 8.
 * last update:    25. 5. 8.
 * author:  gigol
 * purpose:
 */
@Repository
public interface BookSearchRepository extends ElasticsearchRepository<BookSearch, Long> {

	Page<BookSearch> findByExpiredFalseAndNameContaining(String name, Pageable pageable);

	Page<BookSearch> findByExpiredFalseAndAuthorContaining(String author, Pageable pageable);

	// 전체 페이지 조회
	Page<BookSearch> findByExpiredFalse(Pageable pageable);
	// 카테고리별 조회
	Page<BookSearch> findByExpiredFalseAndCategoryMajor(String major, Pageable pageable);

	Optional<BookSearch> findByIdAndExpiredFalse(Long id);
}