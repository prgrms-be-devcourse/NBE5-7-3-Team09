package ninegle.Readio.publisher.dto

import jakarta.validation.constraints.NotBlank


data class PublisherDto(
    val id: Long,
    val name: String
)


data class PublisherRequestDto(
    @field: NotBlank(message = "출판사를 입력해주세요.")
    val name: String
)

data class PublisherResponseDto(
    val id: Long,
    val name: String
)

data class PublisherListResponseDto(
    val publishers: MutableList<PublisherResponseDto>
)
