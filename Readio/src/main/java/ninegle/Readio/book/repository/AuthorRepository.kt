package ninegle.Readio.book.repository

import ninegle.Readio.book.domain.Author
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Readio - AuthorRepository
 * create date:    25. 5. 9.
 * last update:    25. 5. 9.
 * author:  gigol
 * purpose:
 */

interface AuthorRepository : JpaRepository<Author, Long> {
    fun findByName(name: String): Author?
}
