package ninegle.Readio.mypage.service

import io.mockk.*
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.mypage.dto.request.UserUpdateRequestDto
import ninegle.Readio.mypage.dto.response.UserInfoDto
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.UserContextService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.util.*

class MyPageUserServiceTest {

    private lateinit var myPageUserService: MyPageUserService
    private lateinit var userRepository: UserRepository
    private lateinit var userContextService: UserContextService
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        userContextService = mockk()
        user = mockk()

        myPageUserService = MyPageUserService(userRepository, userContextService)
    }

    @Test
    fun `사용자 정보 조회 성공 테스트`() {
        val userId = 1L
        every { userContextService.currentUserId } returns userId
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { user.email } returns "test@example.com"
        every { user.nickname } returns "팀9글"
        every { user.phoneNumber } returns "010-1234-5678"
        every { user.point } returns 15000L

        val result: UserInfoDto = myPageUserService.getUserInfo()

        assertAll(
            { assertEquals("test@example.com", result.email) },
            { assertEquals("팀9글", result.nickname) },
            { assertEquals("010-1234-5678", result.phoneNumber) },
            { assertEquals(15000L, result.point) }
        )

        verify(exactly = 1) { userContextService.currentUserId }
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `사용자 정보 조회 실패 - 사용자가 존재하지 않는 경우`() {
        val userId = 999L
        every { userContextService.currentUserId } returns userId
        every { userRepository.findById(userId) } returns Optional.empty()

        val exception = assertThrows<BusinessException> {
            myPageUserService.getUserInfo()
        }

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.errorCode)
        verify(exactly = 1) { userContextService.currentUserId }
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `닉네임만 업데이트 성공 테스트`() {
        val userId = 1L
        val updateDto = UserUpdateRequestDto(nickname = "새로운닉네임", phoneNumber = null)

        every { userContextService.currentUserId } returns userId
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { user.updateNickname(any()) } just Runs  // Unit 반환 메서드
        every { user.email } returns "test@example.com"
        every { user.nickname } returns "새로운닉네임"
        every { user.phoneNumber } returns "010-1234-5678"
        every { user.point } returns 15000L

        val result: UserInfoDto = myPageUserService.updateUserInfo(updateDto)

        assertAll(
            { assertEquals("test@example.com", result.email) },
            { assertEquals("새로운닉네임", result.nickname) },
            { assertEquals("010-1234-5678", result.phoneNumber) },
            { assertEquals(15000L, result.point) }
        )

        verify(exactly = 1) { user.updateNickname("새로운닉네임") }
        verify(exactly = 0) { user.updatePhoneNumber(any()) }
    }

    @Test
    fun `전화번호만 업데이트 성공 테스트`() {
        val userId = 1L
        val updateDto = UserUpdateRequestDto(nickname = null, phoneNumber = "010-9876-5432")

        every { userContextService.currentUserId } returns userId
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { user.updatePhoneNumber(any()) } just Runs
        every { user.email } returns "test@example.com"
        every { user.nickname } returns "팀9글"
        every { user.phoneNumber } returns "010-9876-5432"
        every { user.point } returns 15000L

        val result: UserInfoDto = myPageUserService.updateUserInfo(updateDto)

        assertAll(
            { assertEquals("test@example.com", result.email) },
            { assertEquals("팀9글", result.nickname) },
            { assertEquals("010-9876-5432", result.phoneNumber) },
            { assertEquals(15000L, result.point) }
        )

        verify(exactly = 1) { user.updatePhoneNumber("010-9876-5432") }
        verify(exactly = 0) { user.updateNickname(any()) }
    }

    @Test
    fun `닉네임과 전화번호 모두 업데이트 성공 테스트`() {
        val userId = 1L
        val updateDto = UserUpdateRequestDto(
            nickname = "새로운닉네임",
            phoneNumber = "010-9876-5432"
        )

        every { userContextService.currentUserId } returns userId
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { user.updateNickname(any()) } just Runs
        every { user.updatePhoneNumber(any()) } just Runs
        every { user.email } returns "test@example.com"
        every { user.nickname } returns "새로운닉네임"
        every { user.phoneNumber } returns "010-9876-5432"
        every { user.point } returns 15000L

        val result: UserInfoDto = myPageUserService.updateUserInfo(updateDto)

        assertAll(
            { assertEquals("test@example.com", result.email) },
            { assertEquals("새로운닉네임", result.nickname) },
            { assertEquals("010-9876-5432", result.phoneNumber) },
            { assertEquals(15000L, result.point) }
        )

        verify(exactly = 1) { user.updateNickname("새로운닉네임") }
        verify(exactly = 1) { user.updatePhoneNumber("010-9876-5432") }
    }

    @Test
    fun `업데이트 실패 - 닉네임과 전화번호 둘 다 비어있는 경우`() {
        val userId = 1L
        val updateDto = UserUpdateRequestDto(nickname = null, phoneNumber = null)

        every { userContextService.currentUserId } returns userId

        val exception = assertThrows<BusinessException> {
            myPageUserService.updateUserInfo(updateDto)
        }

        assertEquals(ErrorCode.MISSING_REQUIRED_FIELD, exception.errorCode)

        // currentUserId는 호출되었지만 Repository는 호출되지 않음
        verify(exactly = 1) { userContextService.currentUserId }
        verify(exactly = 0) { userRepository.findById(any()) }
    }

    @Test
    fun `업데이트 실패 - 닉네임과 전화번호 둘 다 빈 문자열인 경우`() {
        val userId = 1L
        val updateDto = UserUpdateRequestDto(nickname = "", phoneNumber = "")

        every { userContextService.currentUserId } returns userId

        val exception = assertThrows<BusinessException> {
            myPageUserService.updateUserInfo(updateDto)
        }

        assertEquals(ErrorCode.MISSING_REQUIRED_FIELD, exception.errorCode)
    }

    @Test
    fun `업데이트 실패 - 사용자가 존재하지 않는 경우`() {
        val userId = 999L
        val updateDto = UserUpdateRequestDto(nickname = "새로운닉네임", phoneNumber = null)

        every { userContextService.currentUserId } returns userId
        every { userRepository.findById(userId) } returns Optional.empty()

        val exception = assertThrows<BusinessException> {
            myPageUserService.updateUserInfo(updateDto)
        }

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `닉네임 유효성 검증 실패 - 빈 문자열`() {
        val userId = 1L
        val updateDto = UserUpdateRequestDto(nickname = "", phoneNumber = "010-1234-5678")

        every { userContextService.currentUserId } returns userId
        every { userRepository.findById(userId) } returns Optional.of(user)

        val exception = assertThrows<BusinessException> {
            myPageUserService.updateUserInfo(updateDto)
        }

        assertEquals(ErrorCode.MISSING_REQUIRED_FIELD, exception.errorCode)
    }

    @Test
    fun `닉네임 유효성 검증 실패 - 공백만 있는 경우`() {
        val userId = 1L
        val updateDto = UserUpdateRequestDto(nickname = "   ", phoneNumber = "010-1234-5678")

        every { userContextService.currentUserId } returns userId
        every { userRepository.findById(userId) } returns Optional.of(user)

        val exception = assertThrows<BusinessException> {
            myPageUserService.updateUserInfo(updateDto)
        }

        assertEquals(ErrorCode.MISSING_REQUIRED_FIELD, exception.errorCode)
    }

    @Test
    fun `전화번호 유효성 검증 실패 - 빈 문자열`() {
        val userId = 1L
        val updateDto = UserUpdateRequestDto(nickname = "팀9글", phoneNumber = "")

        every { userContextService.currentUserId } returns userId
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { user.updateNickname("팀9글") } just Runs

        val exception = assertThrows<BusinessException> {
            myPageUserService.updateUserInfo(updateDto)
        }

        assertEquals(ErrorCode.MISSING_REQUIRED_FIELD, exception.errorCode)
    }

    @Test
    fun `전화번호 유효성 검증 실패 - 잘못된 형식들`() {
        val invalidPhoneNumbers = listOf(
            "01012345678",      // 하이픈 없음
            "010-123-5678",     // 가운데 번호가 3자리
            "010-12345-678",    // 마지막 번호가 3자리
            "011-1234-5678",    // 010이 아님
            "010-abcd-5678",    // 숫자가 아님
            "010-1234-abcd",    // 숫자가 아님
            "010-1234-56789"    // 마지막 번호가 5자리
        )

        invalidPhoneNumbers.forEach { invalidPhone ->
            val userId = 1L
            val updateDto = UserUpdateRequestDto(nickname = null, phoneNumber = invalidPhone)

            every { userContextService.currentUserId } returns userId
            every { userRepository.findById(userId) } returns Optional.of(user)

            val exception = assertThrows<BusinessException> {
                myPageUserService.updateUserInfo(updateDto)
            }

            assertEquals(ErrorCode.INVALID_REQUEST_DATA, exception.errorCode,
                "전화번호 '$invalidPhone'에 대한 검증이 실패해야 합니다")
        }
    }

    @Test
    fun `전화번호 유효성 검증 성공 - 올바른 형식들`() {
        val validPhoneNumbers = listOf(
            "010-0000-0000",
            "010-1234-5678",
            "010-9999-9999"
        )

        validPhoneNumbers.forEach { validPhone ->
            val userId = 1L
            val updateDto = UserUpdateRequestDto(nickname = null, phoneNumber = validPhone)

            every { userContextService.currentUserId } returns userId
            every { userRepository.findById(userId) } returns Optional.of(user)
            every { user.updatePhoneNumber(any()) } just Runs
            every { user.email } returns "test@example.com"
            every { user.nickname } returns "팀9글"
            every { user.phoneNumber } returns validPhone
            every { user.point } returns 15000L

            val result = myPageUserService.updateUserInfo(updateDto)

            assertEquals(validPhone, result.phoneNumber,
                "전화번호 '$validPhone'에 대한 검증이 성공해야 합니다")
        }
    }
}