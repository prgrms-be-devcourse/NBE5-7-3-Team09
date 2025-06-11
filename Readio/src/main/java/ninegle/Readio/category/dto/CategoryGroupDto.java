package ninegle.Readio.category.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryGroupDto {
	private long id;
	private String major;
	private List<String> subs;
}
