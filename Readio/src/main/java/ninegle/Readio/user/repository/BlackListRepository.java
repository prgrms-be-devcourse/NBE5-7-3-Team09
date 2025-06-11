package ninegle.Readio.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ninegle.Readio.user.domain.BlackList;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {
	Optional<BlackList> findByInversionAccessToken(String token);

}
