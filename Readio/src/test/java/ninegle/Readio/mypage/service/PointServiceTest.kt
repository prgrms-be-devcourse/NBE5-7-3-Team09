package ninegle.Readio.mypage.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.mypage.dto.response.PointResponseDto
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.UserContextService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class PointServiceTest {
    private lateinit var userContextService: UserContextService
    private lateinit var userRepository: UserRepository
    private lateinit var user: User
    private lateinit var pointService: PointService

    @BeforeEach
    fun setUp() {
        userContextService = mockk()
        userRepository = mockk()
        user = mockk()
        pointService = PointService(
            userContextService = userContextService,
            userRepository = userRepository
        )
    }

    @Test
    fun `포인트 조회 성공`() {
        val testUserId = 1L
        val testPoint = 15000L

        every { userContextService.currentUserId } returns testUserId
        every { userRepository.findById(testUserId) } returns Optional.of(user)
        every { user.point } returns testPoint

        val result = pointService.getUserPoints()

        assertEquals(testPoint, result.currentPoint)

        verify(exactly = 1) { userContextService.currentUserId }
        verify(exactly = 1) { userRepository.findById(testUserId) }
        verify(exactly = 1) { user.point }
    }

    @Test
    fun `포인트 조회 성공 - 포인트가 0인 경우`() {
        val testUserId = 2L
        val testPoint = 0L

        every { userContextService.currentUserId } returns testUserId
        every { userRepository.findById(testUserId) } returns Optional.of(user)
        every { user.point } returns testPoint

        val result = pointService.getUserPoints()

        assertEquals(0L, result.currentPoint)
        assertInstanceOf(PointResponseDto::class.java, result)
    }

    @Test
    fun `포인트 조회 실패 - 사용자를 찾을 수 없는 경우`() {
        val testUserId = 999L

        every { userContextService.currentUserId } returns testUserId
        every { userRepository.findById(testUserId) } returns Optional.empty()

        val exception = assertThrows<BusinessException> {
            pointService.getUserPoints()
        }

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.errorCode)

        // currentUserId와 findById는 호출되었지만, point는 호출되지 않아야 함
        verify(exactly = 1) { userContextService.currentUserId }
        verify(exactly = 1) { userRepository.findById(testUserId) }
        verify(exactly = 0) { user.point }
    }

    @Test
    fun `포인트 조회 실패 - Repository에서 예외 발생`() {
        val testUserId = 1L

        every { userContextService.currentUserId } returns testUserId
        every { userRepository.findById(testUserId) } throws RuntimeException("DB 연결 실패")

        val exception = assertThrows<RuntimeException> {
            pointService.getUserPoints()
        }

        assertEquals("DB 연결 실패", exception.message)

        // currentUserId와 findById는 호출되었지만, point는 호출되지 않아야 함
        verify(exactly = 1) { userContextService.currentUserId }
        verify(exactly = 1) { userRepository.findById(testUserId) }
        verify(exactly = 0) { user.point }
    }
}