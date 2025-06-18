package ninegle.Readio.user.adapter

import ninegle.Readio.user.domain.Role
import ninegle.Readio.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails



class UserDetail (
    var id: Long?,
    private var password: String,
    var email: String,
    var role: Role) : UserDetails {


    //인가 검사 시 사용하는 것, 로그인한 사용자(Principal)의 권한(Role)을 설정하는 메서드
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return listOf (SimpleGrantedAuthority("ROLE_" + this.role))
    }

    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String {
        return this.email
    }

    companion object {
		fun UserDetailsMake(finduser: User): UserDetail {
            return UserDetail(
                id = finduser.id,
                email = finduser.email,
                password = finduser.password,
                role = finduser.role)
            }
    }
}

