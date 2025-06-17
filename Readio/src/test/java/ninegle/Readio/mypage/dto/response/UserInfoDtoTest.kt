package ninegle.Readio.mypage.dto.response

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.Assertions.*

class UserInfoDtoTest {
    @Test
    fun `UserInfoDto 생성 테스트`() {
        val email = "test@test.com"
        val nickname = "팀9글"
        val phoneNumber = "010-1234-5678"
        val point = 15000L

        val dto = UserInfoDto(
            email = email,
            nickname = nickname,
            phoneNumber = phoneNumber,
            point = point
        )

        assertAll(
            { assertEquals(email, dto.email) },
            { assertEquals(nickname, dto.nickname) },
            { assertEquals(phoneNumber, dto.phoneNumber) },
            { assertEquals(point, dto.point) }
        )
    }

    @Test
    fun `data class copy 기능 테스트`() {
        val originalDto = UserInfoDto(
            email = "test@test.com",
            nickname = "팀9글",
            phoneNumber = "010-1111-1111",
            point = 15000L
        )

        val copiedDto = originalDto.copy(
            nickname = "나는 누굴까~",
            point = 2000L
        )

        assertAll(
            { assertEquals("test@test.com", copiedDto.email) }, // 기존 값 유지
            { assertEquals("나는 누굴까~", copiedDto.nickname) }, // 변경된 값
            { assertEquals("010-1111-1111", copiedDto.phoneNumber) }, // 기존 값 유지
            { assertEquals(2000L, copiedDto.point) } // 변경된 값
        )
    }

    @Test
    fun `data class 기본 기능들 테스트`() {
        val dto1 = UserInfoDto("test@example.com", "user1", "010-1234-5678", 15000L)
        val dto2 = UserInfoDto("test@example.com", "user1", "010-1234-5678", 15000L)
        val dto3 = UserInfoDto("different@example.com", "user1", "010-1234-5678", 15000L)

        assertAll(
            { assertEquals(dto1, dto2) }, // equals 테스트
            { assertEquals(dto1.hashCode(), dto2.hashCode()) }, // hashCode 테스트
            { assertNotEquals(dto1, dto3) }, // 다른 값은 not equals
            { assertTrue(dto1.toString().contains("test@example.com")) }, // toString 테스트
            { assertTrue(dto1.toString().contains("user1")) },
            { assertTrue(dto1.toString().contains("15000")) }
        )
    }
}