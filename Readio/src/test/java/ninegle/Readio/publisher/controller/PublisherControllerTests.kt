package ninegle.Readio.publisher.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.global.util.genPubReq
import ninegle.Readio.global.util.genPublisherDtoList
import ninegle.Readio.global.util.genPublisherList
import ninegle.Readio.publisher.dto.PublisherListResponseDto
import ninegle.Readio.publisher.dto.PublisherResponseDto
import ninegle.Readio.publisher.service.PublisherService
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import kotlin.test.Test

@WebMvcTest(PublisherController::class)
class PublisherControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var service: PublisherService

    @Autowired
    lateinit var om: ObjectMapper

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `출판사 저장 요청을 보내면 성공적으로 저장하고 저장된 데이터와 응답을 201 Created를 내린다`() {

        val expectedId = 1L
        val expectedName = "한빛미디어"

        val request = genPubReq(expectedName)

        `when`( service.save(request)).thenReturn(PublisherResponseDto(expectedId, expectedName))

        mockMvc.post("/admin/publishers") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(request)
            with(csrf())
        }.andExpect {
            status { isCreated() }
            jsonPath("$.message") { value("출판사 등록이 정상적으로 등록되었습니다.") }
            jsonPath("$.data.id") { value(expectedId) }
            jsonPath("$.data.name") { value(expectedName) }
        }.andDo {
            print()
        }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `출판사 저장 요청을 보낸 데이터가 이미 존재하면 409 예외처리를 응답한다`() {

        val expectedName = "한빛미디어"

        val request = genPubReq(expectedName)

        `when`(service.save(request)).thenThrow(BusinessException(ErrorCode.PUBLISHER_ALREADY_EXISTS))

        mockMvc.post("/admin/publishers") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(request)
            with(csrf())
        }.andExpect {
            status { isConflict() }
            jsonPath("$.status") { value(409) }
            jsonPath("$.code") { value("PUBLISHER_ALREADY_EXISTS") }
            jsonPath("$.message") { value("이미 존재하는 출판사입니다.") }
            jsonPath("$.path") { value("POST /admin/publishers") }
        }.andDo {
            print()
        }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `출판사 데이터가 존재할 경우 전체 조회시 리스트 형태의 PublisherResponseDto를 반환한다`() {
        val publishers = genPublisherList(3)
        val expected = genPublisherDtoList(publishers)

        `when`(service.getPublisherAll()).thenReturn(expected)

        mockMvc.get("/admin/publishers")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value(200) }
                jsonPath("$.data.publishers[0].id") { value(expected.publishers[0].id) }
                jsonPath("$.message") { value("출판사 조회가 정상적으로 수행되었습니다.")}
            }

    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `출판사 데이터가 존재하지 않을 경우 전체 조회시 빈 리스트를 반환한다`() {
        val expected = PublisherListResponseDto(mutableListOf())

        `when`(service.getPublisherAll()).thenReturn(expected)

        mockMvc.get("/admin/publishers")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value(200) }
                jsonPath("$.message") { value("출판사 조회가 정상적으로 수행되었습니다.")}
                jsonPath("$.data.publishers") { isEmpty() }
            }

    }



}