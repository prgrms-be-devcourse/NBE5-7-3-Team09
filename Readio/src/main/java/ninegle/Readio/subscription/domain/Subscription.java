package ninegle.Readio.subscription.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true) // 유저당 하나의 구독만 허용
	private Long userId;

	private LocalDate subDate;

	private LocalDate expDate;

	@Column(nullable = false)
	private boolean canceled;

	@Builder
	public Subscription(Long userId, LocalDate subDate, LocalDate expDate, boolean canceled) {
		this.userId = userId;
		this.subDate = subDate;
		this.expDate = expDate;
		this.canceled = false; // 기본값 설정
	}

	// 구독 기간 갱신
	public void updatePeriod(LocalDate subDate, LocalDate expDate) {
		this.subDate = subDate;
		this.expDate = expDate;
	}

	// 구독 취소
	public void cancel() {
		this.canceled = true;
	}

	// 구독 취소 상태 해제
	public void uncancel() {
		this.canceled = false;
	}

	// 구독이 유효한지 확인
	public boolean isActive() {
		return expDate.isAfter(LocalDate.now()) || expDate.isEqual(LocalDate.now());
	}
}