package ninegle.Readio.user.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.dto.LoginRequestDto
import ninegle.Readio.user.dto.SignUpRequestDto
import ninegle.Readio.user.repository.BlackListRepository
import ninegle.Readio.user.repository.TokenRepository
import ninegle.Readio.user.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.verify
import org.springframework.security.crypto.password.PasswordEncoder
import jakarta.servlet.http.HttpServletResponse



class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val tokenRepository = mockk<TokenRepository>()
    private val jwtTokenProvider = mockk<JwtTokenProvider>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val blackListRepository = mockk<BlackListRepository>()

    private val userService = UserService(
        userRepository, passwordEncoder, jwtTokenProvider, tokenRepository, blackListRepository )

    @Test
    fun `회원가입 성공 테스트`() {
        val dto = SignUpRequestDto("test@example.com", "1234", "test", "010-1111-1111")

        every { userRepository.findByEmail(dto.email) } returns null
        every { passwordEncoder.encode(dto.password) } returns "encoded"
        val slot = slot<User>()  // User 객체를 담을 슬롯 준비
        every { userRepository.save(capture(slot)) } returns User()

        userService.signup(dto)

        verify(exactly = 1) { userRepository.findByEmail(dto.email) }
        verify(exactly = 1) { passwordEncoder.encode(dto.password) }
        verify(exactly = 1) { userRepository.save(any()) }

        // 저장된 User 객체 필드 검증
        assertEquals(dto.email, slot.captured.email)
        assertEquals("encoded", slot.captured.password)
        assertEquals(dto.nickname, slot.captured.nickname)
        println("회원가입 성공")
    }

    @Test
    fun `회원가입 이메일 중복 실패 테스트` (){
        val dto = SignUpRequestDto("test@example.com", "1234", "test", "010-1111-1111")
        every { userRepository.findByEmail(dto.email) } returns User()

        val exception = assertThrows<BusinessException> {
            userService.signup(dto)
        }

        //이미 존재하는 이메일처럼 보이게
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS.message, exception.message)
        println("이메일 중복 예외 발생 테스트 성공")
    }

    @Test
    fun `로그인 성공 테스트`(){
        val dto = LoginRequestDto("test@example.com", "1234")
        val user = User( email = dto.email, password = dto.password, phoneNumber = "010-1111-1111", nickname = "test")
        user.id = 1L


        every { userRepository.findByEmail(dto.email) } returns user
        every { passwordEncoder.matches(dto.password, user.password) } returns true
        every { tokenRepository.findTop1ByUserIdOrderByIdDesc(user.id!!) } returns null // 토큰 없는 경우로 가정해 발급
        every { jwtTokenProvider.issueAccessToken(user.id, user.role, user.email) } returns "access-token"
        every { jwtTokenProvider.issueRefreshToken(user.id, user.role, user.email) } returns "refresh-token"

        // save 호출 모킹
        every { tokenRepository.save(any()) } answers { firstArg() }

        //(relaxed = true) 기본값 자동으로 반환 설정
        val response = mockk<HttpServletResponse>(relaxed = true)

        userService.login(dto,response )

        verify { response.setHeader("Authorization", "Bearer access-token") }
        verify { response.setHeader("Refresh", "refresh-token") }

        println("로그인 성공 테스트 완료")
    }











}

