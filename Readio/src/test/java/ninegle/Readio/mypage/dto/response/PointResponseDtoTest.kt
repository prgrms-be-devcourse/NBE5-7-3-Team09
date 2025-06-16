package ninegle.Readio.mypage.dto.response

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.Assertions.*

class PointResponseDtoTest {

    @Test
    fun `기본 회원가입 포인트 테스트`() {
        val defaultPoint = 15000L
        val dto = PointResponseDto(currentPoint = defaultPoint)

        assertEquals(15000L, dto.currentPoint)
    }

    @Test
    fun `포인트 사용 후 잔여 포인트 테스트`() {
        // 15000원에서 구독권 금액 14900원 사용한 경우
        val remainingPoint = 100L

        val dto = PointResponseDto(currentPoint = remainingPoint)

        assertEquals(100L, dto.currentPoint)
    }

    @Test
    fun `충전된 포인트가 정상 반영되는지 테스트`() {
        val chargeAfterPoint = 50000L

        val dto = PointResponseDto(currentPoint = chargeAfterPoint)

        assertEquals(50000L, dto.currentPoint)
    }

    @Test
    fun `data class 기본 기능 테스트`() {
        val dto1 = PointResponseDto(currentPoint = 15000L)
        val dto2 = PointResponseDto(currentPoint = 15000L)
        val dto3 = PointResponseDto(currentPoint = 25000L)

        assertAll(
            { assertEquals(dto1, dto2) }, // 같은 포인트면 equals
            { assertEquals(dto1.hashCode(), dto2.hashCode()) },
            { assertNotEquals(dto1, dto3) }, // 다른 포인트면 not equals
            { assertTrue(dto1.toString().contains("15000")) },
            { assertTrue(dto1.toString().contains("PointResponseDto")) }
        )
    }

    @Test
    fun `포인트 복사 테스트`() {
        val originalDto = PointResponseDto(currentPoint = 15000L)
        val copiedDto = originalDto.copy(currentPoint = 20000L)

        assertAll(
            { assertEquals(15000L, originalDto.currentPoint) },
            { assertEquals(20000L, copiedDto.currentPoint) },
            { assertNotEquals(originalDto, copiedDto) } // 서로 다른 객체
        )
    }
}