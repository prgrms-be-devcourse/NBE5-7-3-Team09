package ninegle.Readio.book.repository

import ninegle.Readio.book.domain.BookSearch
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Readio - BookSearchRepository
 * create date:    25. 5. 8.
 * last update:    25. 5. 8.
 * author:  gigol
 * purpose:
 */
@Repository
interface BookSearchRepository : ElasticsearchRepository<BookSearch, Long> {

    fun findByExpiredFalseAndNameContaining(name: String, pageable: Pageable): Page<BookSearch>

    fun findByExpiredFalseAndAuthorContaining(author: String, pageable: Pageable): Page<BookSearch>

    // 전체 페이지 조회
    fun findByExpiredFalse(pageable: Pageable): Page<BookSearch>

    // 카테고리별 조회
    fun findByExpiredFalseAndCategoryMajor(major: String, pageable: Pageable): Page<BookSearch>
}