package ninegle.Readio.library.repository

import ninegle.Readio.book.domain.Book
import ninegle.Readio.library.domain.LibraryBook
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LibraryBookRepository : JpaRepository<LibraryBook, Long> {

    @Query("SELECT lb.book FROM LibraryBook lb WHERE lb.library.id = :libraryId")
    fun findBookByLibraryId(@Param("libraryId") libraryId: Long, pageable: Pageable): Page<Book>

    @Query("SELECT lb FROM LibraryBook lb WHERE lb.library.id = :libraryId AND lb.book.id = :bookId")
    fun findLibraryBook(@Param("libraryId") libraryId: Long, @Param("bookId") bookId: Long): LibraryBook?

    @Query("SELECT lb FROM LibraryBook lb WHERE lb.library.id = :libraryId AND lb.book.id = :bookId")
    fun duplicateTest(@Param("libraryId") libraryId: Long, @Param("bookId") bookId: Long?): LibraryBook?

    fun findByLibraryId(libraryId: Long?): List<LibraryBook>
}
