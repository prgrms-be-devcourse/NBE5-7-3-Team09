package ninegle.Readio.category.dto

data class CategoryDto(
    val id: Long,
    val major: String,
    val sub: String
)

data class CategoryGroupDto(
    val id: Long,
    val major: String,
    val subs: MutableList<String>
)

data class CategoryGroupResponseDto(
    val categories: MutableList<CategoryGroupDto>
)