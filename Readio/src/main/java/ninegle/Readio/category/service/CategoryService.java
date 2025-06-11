package ninegle.Readio.category.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.category.domain.Category;
import ninegle.Readio.category.dto.CategoryGroupDto;
import ninegle.Readio.category.dto.CategoryGroupResponseDto;
import ninegle.Readio.category.mapper.CategoryMapper;
import ninegle.Readio.category.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public CategoryGroupResponseDto findCategoryGroup() {
		List<Category> categories = categoryRepository.findAll();

		List<CategoryGroupDto> result = new ArrayList<>();
		Map<String, CategoryGroupDto> map = new HashMap<>();

		for (Category category : categories) {
			String major = category.getMajor();
			String sub = category.getSub();
			long id = category.getId();

			// map에 major가 없다면 새로운 리스트 생성 및 sub 추가
			if (!map.containsKey(major)) {
				CategoryGroupDto group = CategoryMapper.toCategoryGroupDto(id, major);
				group.getSubs().add(sub);

				result.add(group);
				map.put(major, group);
			} else {
				// map에 major가 있다면 리스트에 sub 추기
				map.get(major).getSubs().add(sub);
			}
		}
		return new CategoryGroupResponseDto(result);

	}

}
