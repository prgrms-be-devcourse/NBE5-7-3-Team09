package ninegle.Readio.category.dto;

import lombok.Builder;

@Builder
public record CategoryDto(long id, String major, String sub) {}
