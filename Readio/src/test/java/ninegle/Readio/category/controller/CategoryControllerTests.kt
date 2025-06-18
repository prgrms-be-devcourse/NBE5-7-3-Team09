package ninegle.Readio.category.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ninegle.Readio.category.dto.CategoryGroupResponseDto
import ninegle.Readio.category.service.CategoryService
import ninegle.Readio.global.util.genCategoriesRespDto
import ninegle.Readio.user.repository.BlackListRepository
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.JwtTokenProvider
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Test

@WebMvcTest(CategoryController::class)
class CategoryControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var service: CategoryService

    @MockBean
    lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var blackListRepository: BlackListRepository

    @Autowired
    lateinit var om: ObjectMapper

    @Test
    @WithMockUser(roles = ["USER"])
    fun `카테고리 그룹 조회가 성공적으로 수행되었을 때`() {

        // given
        val expected = genCategoriesRespDto()

        // when
        `when`(service.findCategoryGroup()).thenReturn(expected)

        // then
        mockMvc.get("/category")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value(200) }
                jsonPath("$.data.categories[0].major") { value(expected.categories[0].major) }
                jsonPath("$.data.categories[0].subs[0]") { value(expected.categories[0].subs[0]) }

            }

    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `카테고리 데이터가 존재하지 않을때는 빈 리스트를 반환한다`() {

        // given
        val expected = CategoryGroupResponseDto(mutableListOf())

        // when
        `when`(service.findCategoryGroup()).thenReturn(expected)

        // then
        mockMvc.get("/category")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value(200) }
                jsonPath("$.data.categories") { isEmpty() }
            }
    }


}