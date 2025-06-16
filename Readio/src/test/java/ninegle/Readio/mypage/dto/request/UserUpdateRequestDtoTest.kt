package ninegle.Readio.mypage.dto.request

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class UserUpdateRequestDtoTest {
    @Test
    fun `UserUpdateRequestDto 생성 테스트`() {
        val nickname = "팀9글"
        val phoneNumber = "010-1234-5678"

        val dto = UserUpdateRequestDto(nickname = nickname, phoneNumber = phoneNumber)

        assertAll(
            { assertEquals(nickname, dto.nickname) },
            { assertEquals(phoneNumber, dto.phoneNumber) }
        )
    }

    @Test
    fun `닉네임만 있는 경우`() {
        val nickname = "팀9글"

        val dto = UserUpdateRequestDto(nickname = nickname, phoneNumber = null)

        assertAll(
            { assertEquals(nickname, dto.nickname) },
            { assertNull(dto.phoneNumber) }
        )
    }

    @Test
    fun `핸드폰 번호만 있는 경우`() {
        val phoneNumber = "010-1234-0000"

        val dto = UserUpdateRequestDto(nickname = null, phoneNumber = phoneNumber)

        assertAll(
            { assertNull(dto.nickname) },
            { assertEquals(phoneNumber, dto.phoneNumber) }
        )
    }

    @Test
    fun `모든 필드가 null인 경우`() {
        val dto = UserUpdateRequestDto()

        assertAll(
            { assertNull(dto.nickname) },
            { assertNull(dto.phoneNumber) }
        )
    }

    @Test
    fun `data class copy 기능 테스트 - 닉네임이나, 전화번호 둘중에 일부만 바꾸는 경우`() {
        val originalDto = UserUpdateRequestDto(nickname = "original", phoneNumber = "010-1111-2222")

        val copiedDto = originalDto.copy(nickname = "updated")

        assertAll(
            { assertEquals("updated", copiedDto.nickname) },
            { assertEquals("010-1111-2222", copiedDto.phoneNumber) }, // 기존 값 유지
            { assertNotEquals(originalDto, copiedDto) } // 서로 다른 객체
        )
    }

    @Test
    fun `data class equals와 hashCode 테스트`() {
        val dto1 = UserUpdateRequestDto(nickname = "test", phoneNumber = "010-1234-5678")
        val dto2 = UserUpdateRequestDto(nickname = "test", phoneNumber = "010-1234-5678")
        val dto3 = UserUpdateRequestDto(nickname = "different", phoneNumber = "010-1234-5678")

        assertAll(
            { assertEquals(dto1, dto2) }, // 같은 내용이면 equals
            { assertEquals(dto1.hashCode(), dto2.hashCode()) }, // hashCode도 같음
            { assertNotEquals(dto1, dto3) }, // 다른 내용이면 not equals
            { assertNotEquals(dto1.hashCode(), dto3.hashCode()) } // hashCode도 다름
        )
    }

    @Test
    fun `data class toString 테스트`() {
        val dto = UserUpdateRequestDto(nickname = "testUser", phoneNumber = "010-1234-5678")

        val toStringResult = dto.toString()

        assertAll(
            { assertTrue(toStringResult.contains("testUser")) },
            { assertTrue(toStringResult.contains("010-1234-5678")) },
            { assertTrue(toStringResult.contains("UserUpdateRequestDto")) }
        )
    }
}