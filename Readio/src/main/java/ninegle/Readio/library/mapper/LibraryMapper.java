package ninegle.Readio.library.mapper;

import java.util.List;

import org.springframework.data.domain.Page;

import ninegle.Readio.library.domain.Library;
import ninegle.Readio.library.dto.library.AllLibraryDto;
import ninegle.Readio.library.dto.library.LibraryListResponseDto;
import ninegle.Readio.library.dto.library.NewLibraryRequestDto;
import ninegle.Readio.library.dto.library.NewLibraryResponseDto;
import ninegle.Readio.library.dto.library.UpdateLibraryRequestDto;
import ninegle.Readio.library.dto.library.UpdateLibraryResponseDto;
import ninegle.Readio.user.domain.User;

public class LibraryMapper {

	//DTO에서 라이브러리로
	public static Library toNewLibraryEntity(NewLibraryRequestDto newLibraryRequestDto, User user) {
		return new Library(newLibraryRequestDto.libraryName(), user);
	}

	//라이브러리에서 DTO로
	public static NewLibraryResponseDto fromNewLibraryResponseDto(long libraryid, String libraryname, long userid) {
		NewLibraryResponseDto newLibraryResponseDto = NewLibraryResponseDto.builder()
			.libraryId(libraryid)
			.libraryName(libraryname)
			.userId(userid).build();
		return newLibraryResponseDto;
	}

	//전체 라이브러리 조회
	public static LibraryListResponseDto fromLibraryListResponseDto(Page<Library> librarypage) {
		List<AllLibraryDto> libraryList = librarypage.getContent().stream()
			.map(library -> AllLibraryDto.builder()
				.id(library.getId())
				.libraryName(library.getLibraryName())
				.createAt(library.getCreatedAt())
				.updateAt(library.getUpdatedAt())
				.build()).toList();
		return LibraryListResponseDto.builder()
			.allLibraries(libraryList)
			.totalCount(librarypage.getTotalElements())
			.page(librarypage.getNumber() + 1)
			.size(librarypage.getSize())
			.build();
	}

	//라이브러리 이름 수정
	public static String toLibraryName(UpdateLibraryRequestDto updateLibraryRequestDto) {
		return updateLibraryRequestDto.libraryName();
	}

	//라이브러리 이름 수정 후 Response
	public static UpdateLibraryResponseDto fromUpdateLibraryResponseDto(long libraryid, String libraryname) {
		return UpdateLibraryResponseDto.builder()
			.id(libraryid)
			.libraryName(libraryname).build();
	}
}
