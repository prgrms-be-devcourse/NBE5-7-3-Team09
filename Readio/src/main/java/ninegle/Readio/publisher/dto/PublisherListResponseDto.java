package ninegle.Readio.publisher.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
public record PublisherListResponseDto(List<PublisherResponseDto> publishers) {}
