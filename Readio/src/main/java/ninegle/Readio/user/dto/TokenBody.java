package ninegle.Readio.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ninegle.Readio.user.domain.Role;

@Getter
@RequiredArgsConstructor
public class TokenBody {
	private Long userId;
	private String email;
	private Role role;

	public TokenBody(Long userId, String email, Role role) {
		this.userId = userId;
		this.email = email;
		this.role = role;
	}
}
