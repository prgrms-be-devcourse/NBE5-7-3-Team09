package ninegle.Readio.category.service

import io.mockk.every
import io.mockk.mockk
import ninegle.Readio.category.dto.CategoryGroupDto
import ninegle.Readio.category.repository.CategoryRepository
import ninegle.Readio.global.util.genCategories
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.collections.emptyList

class CategoryServiceTests {

    val repository = mockk<CategoryRepository>()
    val service = CategoryService(repository)


    @Test
    fun `카테고리 조회시 major로 그룹화하여 목록을 보여준다`() {

        // given
        val categories = genCategories()

        // when
        every { repository.findAll() } returns categories

        val actual = service.findCategoryGroup()

        // then
        assertEquals(3, actual.categories.size)

        // 총류
        assertEquals(categories[0].major, actual.categories[0].major)
        assertEquals(categories[0].sub, actual.categories[0].subs[0])
        assertEquals(categories[1].major, actual.categories[0].major)
        assertEquals(categories[1].sub, actual.categories[0].subs[1])
        assertEquals(categories[2].major, actual.categories[0].major)
        assertEquals(categories[2].sub, actual.categories[0].subs[2])
        assertEquals(categories[3].major, actual.categories[0].major)
        assertEquals(categories[3].sub, actual.categories[0].subs[3])

        // 철학
        assertEquals(categories[4].major, actual.categories[1].major)
        assertEquals(categories[4].sub, actual.categories[1].subs[0])
        assertEquals(categories[5].major, actual.categories[1].major)
        assertEquals(categories[5].sub, actual.categories[1].subs[1])
        assertEquals(categories[6].major, actual.categories[1].major)
        assertEquals(categories[6].sub, actual.categories[1].subs[2])

        // 종교
        assertEquals(categories[7].major, actual.categories[2].major)
        assertEquals(categories[7].sub, actual.categories[2].subs[0])
        assertEquals(categories[8].major, actual.categories[2].major)
        assertEquals(categories[8].sub, actual.categories[2].subs[1])
        assertEquals(categories[9].major, actual.categories[2].major)
        assertEquals(categories[9].sub, actual.categories[2].subs[2])

    }

    @Test
    fun `카테고리 데이터가 존재하지 않을 경우 빈 리스트를 반환한다`() {

        // when
        every { repository.findAll() } returns emptyList()

        val actual = service.findCategoryGroup()

        // then
        assertEquals(0, actual.categories.size)
        assertEquals(emptyList<CategoryGroupDto>(),actual.categories)
    }


}