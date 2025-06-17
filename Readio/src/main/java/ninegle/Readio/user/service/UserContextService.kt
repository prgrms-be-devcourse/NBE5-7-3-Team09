package ninegle.Readio.user.service

import ninegle.Readio.user.adapter.UserDetail
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

//알아서 admin 추출되게 만들어야 함
@Component
class UserContextService {
    val currentUserId: Long?
        get() {
            val auth =
                SecurityContextHolder.getContext().authentication

            // Authentication이 null이거나, Principal이 UserDetails가 아닌 경우 예외 처리
            if (auth == null || auth.principal !is UserDetail) {
                throw AccessDeniedException("인증되지 않았습니다.")
            }

            val userDetail = auth.principal as UserDetail
            return userDetail.id // user ID 반환
        }
}