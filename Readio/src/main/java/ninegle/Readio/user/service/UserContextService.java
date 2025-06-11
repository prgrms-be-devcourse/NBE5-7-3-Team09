package ninegle.Readio.user.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ninegle.Readio.user.adapter.UserDetail;

//알아서 admin 추출되게 만들어야 함

@Component
public class UserContextService {

	public Long getCurrentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// Authentication이 null이거나, Principal이 UserDetails가 아닌 경우 예외 처리
		if (auth == null || !(auth.getPrincipal() instanceof UserDetail)) {
			throw new AccessDeniedException("인증되지 않았습니다.");
		}

		UserDetail userDetail = (UserDetail)auth.getPrincipal();
		return userDetail.getId();  // user ID 반환
	}
}