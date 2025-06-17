package ninegle.Readio.library.repository

import ninegle.Readio.library.domain.Library
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LibraryRepository : JpaRepository<Library, Long> {

    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Library>

    fun findByIdAndUserId(id: Long, userId: Long): Library

    fun findAllByUserId(userId: Long): List<Library>
}
