package ninegle.Readio.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.category.dto.CategoryGroupResponseDto;
import ninegle.Readio.category.service.CategoryService;
import ninegle.Readio.global.unit.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public ResponseEntity<BaseResponse<CategoryGroupResponseDto>> getCategoryGroup() {
		CategoryGroupResponseDto response = categoryService.findCategoryGroup();
		return BaseResponse.ok("카테고리 그룹 조회가 성공적으로 수행되었습니다.", response, HttpStatus.OK);
	}
}
