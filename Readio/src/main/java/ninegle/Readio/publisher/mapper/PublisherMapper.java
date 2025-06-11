package ninegle.Readio.publisher.mapper;

import java.util.ArrayList;
import java.util.List;

import ninegle.Readio.publisher.domain.Publisher;
import ninegle.Readio.publisher.dto.PublisherListResponseDto;
import ninegle.Readio.publisher.dto.PublisherResponseDto;

public class PublisherMapper {

	public static PublisherResponseDto toResponseDto(Publisher publisher) {
		return PublisherResponseDto.builder()
			.id(publisher.getId())
			.name(publisher.getName())
			.build();
	}

	public static PublisherListResponseDto toListResponseDto(List<Publisher> publishers) {
		List<PublisherResponseDto> publisherDtos = new ArrayList<>();

		for (Publisher publisher : publishers) {
			publisherDtos.add(toResponseDto(publisher));
		}
		return PublisherListResponseDto.builder()
			.publishers(publisherDtos)
			.build();

	}

}
