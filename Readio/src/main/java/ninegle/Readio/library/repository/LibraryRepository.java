package ninegle.Readio.library.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ninegle.Readio.library.domain.Library;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

	Page<Library> findAllByUserId(Long userId, Pageable pageable);

	Library findByIdAndUserId(Long id, long userId);

	List<Library> findAllByUserId(long userId);

}
