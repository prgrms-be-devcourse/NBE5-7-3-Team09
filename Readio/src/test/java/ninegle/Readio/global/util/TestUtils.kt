package ninegle.Readio.global.util

import ninegle.Readio.publisher.domain.Publisher
import ninegle.Readio.publisher.dto.PublisherListResponseDto
import ninegle.Readio.publisher.dto.PublisherRequestDto
import ninegle.Readio.publisher.dto.PublisherResponseDto
import ninegle.Readio.category.domain.Category
import ninegle.Readio.category.dto.CategoryGroupDto
import ninegle.Readio.category.dto.CategoryGroupResponseDto

// 카테고리 Test Utils
fun genCategory(id: Long, major: String, sub: String) = Category(id, major, sub)

fun genCategories() = mutableListOf(
    genCategory(0, "총류", "총류 일반"),
    genCategory(10, "총류", "도서관학"),
    genCategory(20, "총류", "문헌정보학"),
    genCategory(30, "총류", "백과사전"),
    genCategory(100, "철학", "철학 일반"),
    genCategory(110, "철학", "형이상학"),
    genCategory(120, "철학", "인식론, 인과론, 인간학"),
    genCategory(200, "종교", "종교 일반"),
    genCategory(210, "종교", "비교종교"),
    genCategory(220, "종교", "불교"),
)

fun genCategoriesRespDto() = CategoryGroupResponseDto(
    genCategories().groupBy { it.major }
        .map{ (major, groupCategories) ->
            val id = groupCategories.first().id
            val subs = genCategories().map { it.sub}
            CategoryGroupDto(id, major, subs.toMutableList())
        }.toMutableList()
)



// 출판사 TestUtils
fun genPublisher(id: Long, name: String = "Publisher") = Publisher(id, name)

fun genPubReq(name: String) = PublisherRequestDto(name)

fun genPublisherList(size: Int) = (1..size).map { genPublisher(it.toLong(), "publisher$it") }.toMutableList()

fun genPublisherDtoList(publishers: MutableList<Publisher>) =
    PublisherListResponseDto(publishers.map {
        PublisherResponseDto(it.id!!, it.name)
    }.toMutableList())