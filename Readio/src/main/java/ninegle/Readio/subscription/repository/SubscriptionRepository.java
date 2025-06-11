package ninegle.Readio.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ninegle.Readio.subscription.domain.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	// 사용자 ID로 구독 조회 메서드
	Optional<Subscription> findByUserId(Long userId);

	List<Subscription> findAllByUserId(Long userId);
}