package ninegle.Readio.publisher.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.publisher.domain.Publisher;
import ninegle.Readio.publisher.dto.PublisherListResponseDto;
import ninegle.Readio.publisher.dto.PublisherRequestDto;
import ninegle.Readio.publisher.dto.PublisherResponseDto;
import ninegle.Readio.publisher.mapper.PublisherMapper;
import ninegle.Readio.publisher.repository.PublisherRepository;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;

@Service
@RequiredArgsConstructor
public class PublisherService {

	private final PublisherRepository publisherRepository;

	@Transactional
	public PublisherResponseDto save(PublisherRequestDto dto) {

		if (publisherRepository.findByName(dto.getName()).isPresent()) {
			throw new BusinessException(ErrorCode.PUBLISHER_ALREADY_EXISTS);
		}

		return PublisherMapper.toResponseDto(publisherRepository.save(new Publisher(dto.getName())));
	}

	@Transactional(readOnly = true)
	public PublisherListResponseDto getPublisherAll() {
		List<Publisher> response = publisherRepository.findAll();

		return  PublisherMapper.toListResponseDto(response);
	}


}
