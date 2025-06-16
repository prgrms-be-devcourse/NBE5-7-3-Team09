package ninegle.Readio.publisher.service

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.util.genPubReq
import ninegle.Readio.global.util.genPublisher
import ninegle.Readio.global.util.genPublisherList
import ninegle.Readio.publisher.repository.PublisherRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows



class PublisherServiceTest {

    val repository = mockk<PublisherRepository>()
    val service = PublisherService(repository)

    @Test
    fun `출판사 저장이 정상적으로 수행된다면 PublisherResponseDto가 반환된다`() {

        // given
        val expectedId = 1L
        val expectedPublisherName = "한빛미디어"

        val request = genPubReq(expectedPublisherName)
        val publisher = genPublisher(expectedId, expectedPublisherName)

        every { repository.findByName(expectedPublisherName) } returns null
        every { repository.save(any()) } returns publisher

        // when
        val actual = service.save(request)

        // then
        actual.id shouldBe expectedId
        actual.name shouldBe expectedPublisherName
    }

    @Test
    fun `출판사 저장시 출판사 이름이 이미 존재한다면 BusinessException 예외가 발생한다`() {

        // given
        val expectedId = 1L
        val expectedPublisherName = "한빛미디어"

        val request = genPubReq(expectedPublisherName)

        every { repository.findByName(expectedPublisherName) } returns genPublisher(expectedId, expectedPublisherName)

        // when
        val actual = assertThrows<BusinessException> { service.save(request) }

        verify(exactly = 1) { repository.findByName(expectedPublisherName) }

        // then
        actual.message shouldBe "이미 존재하는 출판사입니다."
        actual.errorCode.name shouldBe "PUBLISHER_ALREADY_EXISTS"
        actual.errorCode.status.value() shouldBe 409

    }

    @Test
    fun `출판사의 데이터가 존재할 경우 리스트형태의 PublisherResponseDto를 반환한다`() {

        val expected = genPublisherList(3)

        every { repository.findAll() } returns expected

        val actual = service.getPublisherAll()

        actual.publishers.size shouldBe 3

        actual.publishers[0].id shouldBe expected[0].id
        actual.publishers[0].name shouldBe expected[0].name

        actual.publishers[1].id shouldBe expected[1].id
        actual.publishers[1].name shouldBe expected[1].name

        actual.publishers[2].id shouldBe expected[2].id
        actual.publishers[2].name shouldBe expected[2].name

    }

    @Test
    fun `출판사의 데이터가 존재하지 않을 경우 빈 리스트를 반환`() {
        every { repository.findAll() } returns emptyList()

        val actual = service.getPublisherAll()

        actual.publishers shouldBe emptyList()
    }

}