package ninegle.Readio.user.config

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.user.adapter.UserDetail
import ninegle.Readio.user.repository.BlackListRepository
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.JwtTokenProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.*

@Slf4j
@Component
// TossApiClient 있는 패키지
@EnableWebSecurity
class JwtAuthFilter(//토큰 제공자
    private val jwtTokenProvider: JwtTokenProvider,
//    private val userService: UserService,
    private val userRepository: UserRepository,
    private val blackListRepository: BlackListRepository
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain)
    {


        val token = resolveToken(request) //헤더에서 토큰값 추출

        // 블랙리스트에 등록된 토큰인지 먼저 검사
        token?.let {
            val blacklist = blackListRepository.findByInversionAccessToken(it)
            if (blacklist != null && blacklist.expiration.after(Date())) {
                throw JwtException("이 토큰은 블랙리스트에 등록되어 있으므로 사용할 수 없습니다.")
            }
        }


        if (token != null && jwtTokenProvider.validate(token)) {
            // 토큰에서 사용자 정보를 추출

            val tokenBody = jwtTokenProvider.parseJwt(token)
            val userEntity = userRepository.findById(tokenBody.userId) ?: throw BusinessException(ErrorCode.USER_NOT_FOUND) //404


//            //사용자가 입력한 ID/PW를 UsernamePasswordAuthenticationToken으로 감쌈
//            val usernamePasswordAuthenticationToken: Authentication = UsernamePasswordAuthenticationToken(
//                userDetail,
//                token, userDetail.authorities
//            )
            val userDetail = UserDetail(
                id = userEntity.get().id,
                email = userEntity.get().email,
                password = userEntity.get().password,
                role = userEntity.get().role
            )
//            val roleList = listOf(SimpleGrantedAuthority("ROLE_USER"))

            val usernamePasswordAuthenticationToken: Authentication =
                UsernamePasswordAuthenticationToken( userDetail, token, userDetail.authorities)

            //SecurityContex는 현재 HTTP 요청에 대한 인증 정보를 저장하는 곳으로 사용자 정보를 spring security가 관리 가능하게 해줌
            SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken

            //기존 인증으로 있던 validate는 토큰이 위조되지 않았는가?, 서명은 맞는가?, 만료됐는가? 같은 기본적인 무결성 검사만 하므로 인증된 사용자란 보장은 되지 않음
            // Spring Security의 필터 체인은 여전히 "이 요청은 인증된 사용자 인지 물어보기 때문에 spring에게 이 사용자는 인증이 되었음을 알려 필터 체인을 통과시킨다
        }
        filterChain.doFilter(request, response)
    }

    //요청을 받아서 헤더가 있다면 "해더에서" 토큰을 추출하는 용도
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }
}
