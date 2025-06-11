package ninegle.Readio.user.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String refreshToken;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	//단방향으로 유저와 연결
	private User user;

	@UpdateTimestamp
	private LocalDateTime UpdateTimeAt;

	@Builder
	public RefreshToken(String refreshToken, User user) {
		this.refreshToken = refreshToken;
		this.user = user;
	}

	public void newSetRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;//갱신 시간 업데이트 (옵션)
	}
}
