package ninegle.Readio.mypage.mapper

import ninegle.Readio.mypage.dto.response.PointResponseDto
import ninegle.Readio.user.domain.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*

/**
 * 지금 User가 java코드로 되어있어서 호환성 문제로인해서
 * 자꾸 test코드에 오류가 발생하기때문에, 얘도 동일하게 일단 mock 기반테스트로 진행함
 */
class PointMapperTest {
    private lateinit var pointMapper: PointMapper

    @BeforeEach
    fun setUp() {
        pointMapper = PointMapper()
    }

    @Test
    fun `기본 회원가입 포인트를 PointResponseDto로 변환 테스트`() {
        val mockUser = mock(User::class.java)
        `when`(mockUser.point).thenReturn(15000L)  // 기본 회원가입 포인트

        val dto: PointResponseDto = pointMapper.toDto(mockUser)

        assertEquals(15000L, dto.currentPoint)
    }

    @Test
    fun `포인트 사용 후 잔여 포인트 변환 테스트`() {
        // 15000원에서 14900원 사용한 경우
        val mockUser = mock(User::class.java)
        `when`(mockUser.point).thenReturn(100L)

        val dto: PointResponseDto = pointMapper.toDto(mockUser)

        assertEquals(100L, dto.currentPoint)
    }

    @Test
    fun `포인트 충전 후 추가된 포인트 변환 테스트`() {
        // 포인트 충전으로 변환된 포인트
        val mockUser = mock(User::class.java)
        `when`(mockUser.point).thenReturn(50000L)

        val dto: PointResponseDto = pointMapper.toDto(mockUser)

        assertEquals(50000L, dto.currentPoint)
    }

    @Test
    fun `사용자가 큰 금액의 포인트 충전해도 포인트 변환이 잘일어나는지 테스트`() {
        // 사용자의 큰금액 충전 포인트
        val mockUser = mock(User::class.java)
        `when`(mockUser.point).thenReturn(1_000_000L)  // 100만원

        val dto: PointResponseDto = pointMapper.toDto(mockUser)

        assertEquals(1_000_000L, dto.currentPoint)
    }

    @Test
    fun `여러 User 객체 변환 테스트`() {
        // given
        val mockUser1 = mock(User::class.java)
        val mockUser2 = mock(User::class.java)
        val mockUser3 = mock(User::class.java)

        `when`(mockUser1.point).thenReturn(15000L)  // 기본 사용자
        `when`(mockUser2.point).thenReturn(25000L)  // 포인트 충전한 사용자
        `when`(mockUser3.point).thenReturn(5000L)   // 포인트 사용한 사용자

        val dto1 = pointMapper.toDto(mockUser1)
        val dto2 = pointMapper.toDto(mockUser2)
        val dto3 = pointMapper.toDto(mockUser3)

        assertAll(
            { assertEquals(15000L, dto1.currentPoint) },
            { assertEquals(25000L, dto2.currentPoint) },
            { assertEquals(5000L, dto3.currentPoint) },
            { assertNotEquals(dto1.currentPoint, dto2.currentPoint) },
            { assertNotEquals(dto2.currentPoint, dto3.currentPoint) }
        )
    }

    @Test
    fun `Mock 객체와의 상호작용 검증`() {
        val mockUser = mock(User::class.java)
        `when`(mockUser.point).thenReturn(15000L)

        pointMapper.toDto(mockUser)

        // Mock 객체의 point 메서드가 정확히 1번 호출되었는지 검증
        verify(mockUser, times(1)).point
        verifyNoMoreInteractions(mockUser)  // 다른 메서드는 호출되지 않았는지 확인
    }

    @Test
    fun `변환된 DTO가 올바른 타입인지 확인`() {
        val mockUser = mock(User::class.java)
        `when`(mockUser.point).thenReturn(15000L)

        val result = pointMapper.toDto(mockUser)

        assertAll(
            { assertNotNull(result) },
            { assertTrue(result is PointResponseDto) },
            { assertEquals(PointResponseDto::class.java, result.javaClass) }
        )
    }

    @Test
    fun `PointMapper가 올바르게 초기화되는지 확인`() {
        assertAll(
            { assertNotNull(pointMapper) },
            { assertTrue(pointMapper is PointMapper) }
        )
    }

}