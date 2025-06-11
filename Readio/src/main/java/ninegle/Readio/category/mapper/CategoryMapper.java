package ninegle.Readio.category.mapper;

import java.util.ArrayList;

import ninegle.Readio.category.dto.CategoryGroupDto;

public class CategoryMapper {

	public static CategoryGroupDto toCategoryGroupDto(long id, String major) {
		return CategoryGroupDto.builder()
			.id(id)
			.major(major)
			.subs(new ArrayList<>())
			.build();
	}
}
