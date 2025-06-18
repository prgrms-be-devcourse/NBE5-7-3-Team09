package ninegle.Readio.category.mapper

import ninegle.Readio.category.dto.CategoryGroupDto

fun toCategoryGroupDto(id: Long, major: String): CategoryGroupDto {
    return CategoryGroupDto(
        id = id,
        major = major,
        subs = mutableListOf()
    )
}
