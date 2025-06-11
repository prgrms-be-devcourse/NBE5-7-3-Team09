package ninegle.Readio.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ninegle.Readio.book.domain.Author;

/**
 * Readio - AuthorRepository
 * create date:    25. 5. 9.
 * last update:    25. 5. 9.
 * author:  gigol
 * purpose: 
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {

	Optional<Author> findByName(String name);

}
