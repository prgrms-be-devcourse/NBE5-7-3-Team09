package ninegle.Readio.category.service

import ninegle.Readio.category.dto.CategoryGroupDto
import ninegle.Readio.category.dto.CategoryGroupResponseDto
import ninegle.Readio.category.repository.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    @Transactional(readOnly = true)
    fun findCategoryGroup(): CategoryGroupResponseDto {
        val categories = categoryRepository.findAll()

        val grouped = categories.groupBy { it.major }

        val result = grouped.map { (major, groupCategories) ->
            CategoryGroupDto(
                id = groupCategories.first().id,
                major = major,
                subs = groupCategories.map { it.sub }.toMutableList()
            )}.toMutableList()

        return CategoryGroupResponseDto(result)
    }
}
