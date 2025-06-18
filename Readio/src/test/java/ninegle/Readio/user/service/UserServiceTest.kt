package ninegle.Readio.user.service

import io.mockk.*
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
import org.springframework.security.crypto.password.PasswordEncoder
import jakarta.servlet.http.HttpServletResponse
import ninegle.Readio.book.repository.PreferencesRepository
import ninegle.Readio.book.repository.ReviewRepository
import ninegle.Readio.library.repository.LibraryBookRepository
import ninegle.Readio.library.repository.LibraryRepository
import ninegle.Readio.mail.user.service.UserMailSender
import ninegle.Readio.subscription.repository.SubscriptionRepository
import ninegle.Readio.user.domain.BlackList
import ninegle.Readio.user.domain.RefreshToken
import ninegle.Readio.user.dto.RefreshTokenRequestDto
import ninegle.Readio.user.dto.TokenBody
import ninegle.Readio.user.util.UserUtil
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.autoconfigure.security.SecurityProperties
import java.util.*


class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val tokenRepository = mockk<TokenRepository>()
    private val jwtTokenProvider = mockk<JwtTokenProvider>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val blackListRepository = mockk<BlackListRepository>()
    private val userMailSender = mockk<UserMailSender>()
    private val reviewRepository = mockk<ReviewRepository>()
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val subscriptionRepository = mockk<SubscriptionRepository>()
    private val libraryRepository = mockk<LibraryRepository>()
    private val libraryBookRepository = mockk<LibraryBookRepository>()

    private val userService = UserService(
        userRepository, passwordEncoder, jwtTokenProvider, tokenRepository, blackListRepository, userMailSender,
        reviewRepository, preferencesRepository, subscriptionRepository, libraryRepository, libraryBookRepository)

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = UserUtil.createTestUser()
    }

    @Test
    fun `회원가입 성공 테스트`() {
        val dto = SignUpRequestDto("test@example.com", "1234", "test", "010-1111-1111")

        every { userRepository.findByEmail(dto.email) } returns null
        every { passwordEncoder.encode(dto.password) } returns "encoded"
        val slot = slot<User>()  // User 객체를 담을 슬롯 준비

        every { userRepository.save(capture(slot)) } returns user
        every { userMailSender.sendSignupMail(any()) } just runs

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
    fun `회원가입 실패 테스트 (이메일 중복)` (){
        val dto = SignUpRequestDto("test@example.com", "1234", "test", "010-1111-1111")

        every { userRepository.findByEmail(dto.email) } returns user

        val exception = assertThrows<BusinessException> {
            userService.signup(dto)
        }

        //이미 존재하는 이메일처럼 보이게
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS.message, exception.message)
        println("이메일 중복 예외 발생 테스트 성공")
    }

    @Test
    fun `로그인 성공 테스트 (access Token과 refresh Token도 발급 되는지)`(){
        val dto = LoginRequestDto("test@example.com", "1234")

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

        verify (exactly = 1) { response.setHeader("Authorization", "Bearer access-token") }
        verify (exactly = 1) { response.setHeader("Refresh", "refresh-token") }

        println("로그인 성공 테스트 완료")
    }

    @Test
    fun `로그인 실패 테스트 (정보가 없는 사용자 입력)`(){
        val dto = LoginRequestDto("test@example.com", "1234")

        every { userRepository.findByEmail(dto.email) } returns null
        every { passwordEncoder.matches(dto.password, user.password) } returns true
        every { tokenRepository.findTop1ByUserIdOrderByIdDesc(user.id!!) } returns null // 토큰 없는 경우로 가정해 발급
        every { jwtTokenProvider.issueAccessToken(user.id, user.role, user.email) } returns "access-token"
        every { jwtTokenProvider.issueRefreshToken(user.id, user.role, user.email) } returns "refresh-token"

        // save 호출 모킹
        every { tokenRepository.save(any()) } answers { firstArg() }

        //(relaxed = true) 기본값 자동으로 반환 설정
        val response = mockk<HttpServletResponse>(relaxed = true)

        // when & then
        val exception = assertThrows<BusinessException> {
            userService.login(dto,response )
        }

        assertEquals("로그인 정보와 일치하는 사용자가 존재하지 않습니다.", exception.message)

        //이메일 탐색은 한번 일어나지만
        verify(exactly = 1) { userRepository.findByEmail(dto.email) }
        //request로 넘어온 유저의 정보는 없으므로 비밀번호 검사는 수행하지 않음
        verify(exactly = 0) { passwordEncoder.matches(any(), any()) }

        println("로그아웃 실패 성공")
    }



    @Test
    fun `토큰 재발급 테스트`() {
        val oldRefreshToken = "old-refresh-token"
        val newRefreshToken = "new-refresh-token"
        val newAccessToken = "new-access-token"

        every { jwtTokenProvider.validate(oldRefreshToken) } returns true
        every { jwtTokenProvider.parseJwt(oldRefreshToken) } returns TokenBody(user.id,user.email,user.role)

        every { userRepository.findById(user.id!!) } returns Optional.of(user)

        val savedRefreshToken = RefreshToken(oldRefreshToken, user)
        every { tokenRepository.findTop1ByUserIdOrderByIdDesc(user.id!!) } returns savedRefreshToken

        // 새로 발급
        every { jwtTokenProvider.issueAccessToken(user.id, user.role, user.email) } returns newAccessToken
        every { jwtTokenProvider.issueRefreshToken(user.id, user.role, user.email) } returns newRefreshToken


        //(relaxed = true) 기본값 자동으로 반환 설정
        val response = mockk<HttpServletResponse>(relaxed = true)

        userService.reissue(oldRefreshToken, response)

        // Then: 응답 헤더가 올바르게 설정되었는지 확인
        verify { response.setHeader("Authorization", "Bearer $newAccessToken") }
        verify { response.setHeader("Refresh", newRefreshToken) }

        println("토큰 재발급 성공 테스트 완료")
    }

    @Test
    fun `로그아웃 성공 테스트`() {
        val refreshTokenRequestDto = RefreshTokenRequestDto("refresh-token")

        val accessToken = "access-token"
        val refreshToken = refreshTokenRequestDto.refreshToken

        every { jwtTokenProvider.validate(accessToken) } returns true
        every { jwtTokenProvider.parseJwt(accessToken) } returns TokenBody(user.id,user.email,user.role)
        every { userRepository.findById(user.id!!) } returns Optional.of(user)

        val savedRefreshToken = RefreshToken(refreshToken, user)
        every { tokenRepository.findTop1ByUserIdOrderByIdDesc(user.id) } returns savedRefreshToken

        // DB에서 조회한 refreshToken 값과 로그아웃 요청 시 사용한 refreshToken이 같아야 함
        assertEquals(savedRefreshToken.refreshToken, (refreshToken))

        every { jwtTokenProvider.getExpiration(accessToken) } returns Date(6000000)

        //DB에 토큰과 같다면 지금 사용한 accessToken을 다시 사용하지 못하게 블랙리스트에 저장
        every { blackListRepository.save(any())} answers { firstArg() }
        every {tokenRepository.delete(savedRefreshToken)} just runs

        userService.logout(accessToken,refreshTokenRequestDto)

        verify(exactly = 1) { jwtTokenProvider.validate(accessToken) }
        verify(exactly = 1) { jwtTokenProvider.parseJwt(accessToken) }
        verify(exactly = 1) { tokenRepository.findTop1ByUserIdOrderByIdDesc(user.id) }
        verify(exactly = 1) { jwtTokenProvider.getExpiration(accessToken) }
        verify(exactly = 1) { blackListRepository.save(any()) }
        verify(exactly = 1) { tokenRepository.delete(savedRefreshToken) }

        println("로그아웃 성공 테스트 완료")

    }




}