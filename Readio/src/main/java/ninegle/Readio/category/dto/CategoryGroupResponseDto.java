package ninegle.Readio.category.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
public record CategoryGroupResponseDto(List<CategoryGroupDto> categories) {}
