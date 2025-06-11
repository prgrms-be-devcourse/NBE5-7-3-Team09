package ninegle.Readio.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ninegle.Readio.book.domain.Book;
import ninegle.Readio.library.domain.LibraryBook;

@Repository
public interface LibraryBookRepository extends JpaRepository<LibraryBook, Long> {

	@Query("SELECT lb.book FROM LibraryBook lb WHERE lb.library.id = :libraryId")
	Page<Book> findBookByLibraryId(@Param("libraryId") Long libraryId, Pageable pageable);

	@Query("SELECT lb FROM LibraryBook lb WHERE lb.library.id = :libraryId AND lb.book.id = :bookId")
	Optional<LibraryBook> findLibraryBoook(@Param("libraryId") Long libraryId,
		@Param("bookId") Long bookId);

	@Query("SELECT lb FROM LibraryBook lb WHERE lb.library.id = :libraryId AND lb.book.id = :bookId")
	Optional<LibraryBook> duplicateTest(@Param("libraryId") Long libraryId, @Param("bookId") Long bookId);

	List<LibraryBook> findByLibraryId(Long libraryId);
}
