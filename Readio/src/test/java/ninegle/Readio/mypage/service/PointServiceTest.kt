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

        every { userContextService.getCurrentUserId() } returns testUserId
        every { userRepository.findById(testUserId) } returns Optional.of(user)
        every { user.point } returns testPoint //user코틀린으로 변환된다면 다시 한번 체크해야함

        val result = pointService.getUserPoints()

        assertEquals(testPoint, result.currentPoint)

        // 각 메서드가 정확히 한 번씩 호출되었는지 확인
        verify(exactly = 1) { userContextService.getCurrentUserId() }
        verify(exactly = 1) { userRepository.findById(testUserId) }
        verify(exactly = 1) { user.point } //여기도 user코틀린으로 변환된다면 다시 한번 체크해야함
    }

    @Test
    fun `포인트 조회 성공 - 포인트가 0인 경우`() {
        val testUserId = 2L
        val testPoint = 0L

        every { userContextService.getCurrentUserId() } returns testUserId
        every { userRepository.findById(testUserId) } returns Optional.of(user)
        every { user.point } returns testPoint //여기도 user코틀린으로 변환시 확인

        val result = pointService.getUserPoints()

        assertEquals(0L, result.currentPoint)
        assertInstanceOf(PointResponseDto::class.java, result)
    }

    @Test
    fun `포인트 조회 실패 - 사용자를 찾을 수 없는 경우`() {
        val testUserId = 999L

        every { userContextService.getCurrentUserId() } returns testUserId
        every { userRepository.findById(testUserId) } returns Optional.empty()

        val exception = assertThrows<BusinessException> {
            pointService.getUserPoints()
        }

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.errorCode)

        //  getCurrentUserId와 findById는 호출되었지만, point는 호출되지 않아야 함
        verify(exactly = 1) { userContextService.getCurrentUserId() }
        verify(exactly = 1) { userRepository.findById(testUserId) }
        verify(exactly = 0) { user.point } //여기도 user코틀린 변환시에 확인
    }

    @Test
    fun `포인트 조회 실패 - Repository에서 예외 발생`() {
        val testUserId = 1L
        every { userContextService.getCurrentUserId() } returns testUserId
        every { userRepository.findById(testUserId) } throws RuntimeException("DB 연결 실패")

        val exception = assertThrows<RuntimeException> {
            pointService.getUserPoints()
        }

        assertEquals("DB 연결 실패", exception.message)

        //  getCurrentUserId와 findById는 호출되었지만, point는 호출되지 않아야 함
        verify(exactly = 1) { userContextService.getCurrentUserId() }
        verify(exactly = 1) { userRepository.findById(testUserId) }
        verify(exactly = 0) { user.point } //여기도 나중에 user변환시 확인
    }
}