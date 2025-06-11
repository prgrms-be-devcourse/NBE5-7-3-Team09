package ninegle.Readio.publisher.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ninegle.Readio.publisher.domain.Publisher;

/**
 * Readio - PublisherRepository
 * create date:    25. 5. 9.
 * last update:    25. 5. 9.
 * author:  gigol
 * purpose:
 */
@Repository
public interface PublisherRepository extends JpaRepository<Publisher,Long> {

	Optional<Publisher> findByName(String name);
}

