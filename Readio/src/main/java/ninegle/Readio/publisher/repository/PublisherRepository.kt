package ninegle.Readio.publisher.repository

import ninegle.Readio.publisher.domain.Publisher
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Readio - PublisherRepository
 * create date:    25. 5. 9.
 * last update:    25. 5. 9.
 * author:  gigol
 * purpose:
 */

interface PublisherRepository : JpaRepository<Publisher, Long> {
    fun findByName(name: String): Publisher?
}

