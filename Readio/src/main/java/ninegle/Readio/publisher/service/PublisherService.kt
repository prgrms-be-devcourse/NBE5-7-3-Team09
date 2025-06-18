package ninegle.Readio.publisher.service

import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.publisher.domain.Publisher
import ninegle.Readio.publisher.dto.PublisherListResponseDto
import ninegle.Readio.publisher.dto.PublisherRequestDto
import ninegle.Readio.publisher.dto.PublisherResponseDto
import ninegle.Readio.publisher.mapper.toListResponseDto
import ninegle.Readio.publisher.mapper.toResponseDto
import ninegle.Readio.publisher.repository.PublisherRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PublisherService(
    private val publisherRepository: PublisherRepository
) {

    @Transactional
    fun save(dto: PublisherRequestDto): PublisherResponseDto {
        val findPublisher = publisherRepository.findByName(dto.name)

        if (findPublisher != null) {
            throw BusinessException(ErrorCode.PUBLISHER_ALREADY_EXISTS)
        }
        val newPublisher = Publisher(name = dto.name)
        val savedPublisher = publisherRepository.save(newPublisher)

        return savedPublisher.toResponseDto()
    }

    @Transactional(readOnly = true)
    fun getPublisherAll() = publisherRepository.findAll().toListResponseDto()

}
