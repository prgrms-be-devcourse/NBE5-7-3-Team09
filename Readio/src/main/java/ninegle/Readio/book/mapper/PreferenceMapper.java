package ninegle.Readio.book.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.adapter.config.NCloudStorageConfig;
import ninegle.Readio.book.domain.Book;
import ninegle.Readio.book.domain.Preference;
import ninegle.Readio.book.dto.preferencedto.PreferenceResponseDto;
import ninegle.Readio.book.dto.preferencedto.PreferenceListResponseDto;
import ninegle.Readio.book.dto.PaginationDto;
import ninegle.Readio.user.domain.User;

/**
 * Readio - PreferenceMapper
 * create date:    25. 5. 13.
 * last update:    25. 5. 13.
 * author:  gigol
 * purpose: 
 */
@Component
@RequiredArgsConstructor
public class PreferenceMapper {

	private final NCloudStorageConfig nCloudStorageConfig;

	public Preference toEntity(User user, Book book){
		return Preference.builder()
			.user(user)
			.book(book)
			.build();
	}
	public PreferenceResponseDto toPreferenceDto(Preference preference){

		return PreferenceResponseDto.builder()
			.id(preference.getBook().getId())
			.name(preference.getBook().getName())
			.image(nCloudStorageConfig.toImageUrl(preference.getBook().getImage()))
			.rating(preference.getBook().getRating())
			.build();
	}
	public List<PreferenceResponseDto> toPreferenceDto(List<Preference> preferences){
		List<PreferenceResponseDto> preferenceResponseDtos = new ArrayList<>();
		for (Preference preference : preferences) {
			preferenceResponseDtos.add(toPreferenceDto(preference));
		}
		return preferenceResponseDtos;
	}
	public PreferenceListResponseDto toPreferenceListDto(List<PreferenceResponseDto> preferenceResponseDtos, PaginationDto paginationDto){
		return PreferenceListResponseDto.builder()
			.preferences(preferenceResponseDtos)
			.pagination(paginationDto)
			.build();
	}
	public PaginationDto toPaginationDto(Long count,int page,int size){
		return PaginationDto.builder()
			.totalPages((count.intValue()/size)+1)
			.size(size)
			.currentPage(page)
			.totalElements(count)
			.build();
	}
}
