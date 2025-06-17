package ninegle.Readio.book.repository

import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.Preference
import ninegle.Readio.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PreferencesRepository : JpaRepository<Preference, Long> {
    fun findPreferenceByBookAndUser(book: Book, user: User): Preference?

    fun countByUser(user: User): Long

    fun findPreferencesByUser(user: User, pageable: Pageable): Page<Preference>?

    fun findAllByUserId(userId: Long): List<Preference>?
}
