package ninegle.Readio.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ninegle.Readio.book.domain.Book;
import ninegle.Readio.book.domain.Preference;
import ninegle.Readio.user.domain.User;

/**
 * Readio - PreferencesRepository
 * create date:    25. 5. 13.
 * last update:    25. 5. 13.
 * author:  gigol
 * purpose: 
 */
public interface PreferencesRepository extends JpaRepository<Preference, Long> {
	Optional<Preference> findPreferenceByBookAndUser(Book book, User user);

	long countByUser(User user);

	Page<Preference> findPreferencesByUser(User user, Pageable pageable);

	List<Preference> findAllByUserId(Long userId);
}
