package ninegle.Readio.publisher.mapper

import ninegle.Readio.publisher.domain.Publisher
import ninegle.Readio.publisher.dto.PublisherListResponseDto
import ninegle.Readio.publisher.dto.PublisherResponseDto



fun Publisher.toResponseDto(): PublisherResponseDto {
    return PublisherResponseDto(
        id = this.id!!,
        name = this.name
    )
}

fun MutableList<Publisher>.toListResponseDto(): PublisherListResponseDto {
    val publisherDtos: MutableList<PublisherResponseDto> = this.map { it.toResponseDto() }.toMutableList()

    return PublisherListResponseDto(
        publishers = publisherDtos
    )
}

