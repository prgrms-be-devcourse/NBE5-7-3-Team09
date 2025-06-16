package ninegle.Readio.category.controller

import ninegle.Readio.category.dto.CategoryGroupResponseDto
import ninegle.Readio.category.service.CategoryService
import ninegle.Readio.global.unit.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/category")
class CategoryController(
    private val categoryService: CategoryService
) {

    @GetMapping
    fun getCategoryGroup(): ResponseEntity<BaseResponse<CategoryGroupResponseDto>> {
        val response = categoryService.findCategoryGroup()
        return BaseResponse.ok(
            "카테고리 그룹 조회가 성공적으로 수행되었습니다.",
            response,
            HttpStatus.OK
        )
    }
}
