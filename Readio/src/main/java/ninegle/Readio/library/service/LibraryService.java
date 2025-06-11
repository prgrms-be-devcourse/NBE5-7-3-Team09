package ninegle.Readio.library.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.library.domain.Library;
import ninegle.Readio.library.dto.library.LibraryListResponseDto;
import ninegle.Readio.library.dto.library.NewLibraryRequestDto;
import ninegle.Readio.library.dto.library.NewLibraryResponseDto;
import ninegle.Readio.library.dto.library.UpdateLibraryRequestDto;
import ninegle.Readio.library.dto.library.UpdateLibraryResponseDto;
import ninegle.Readio.library.mapper.LibraryMapper;
import ninegle.Readio.library.repository.LibraryRepository;
import ninegle.Readio.user.domain.User;
import ninegle.Readio.user.repository.UserRepository;
import ninegle.Readio.user.service.UserContextService;

@Service
@RequiredArgsConstructor
//트랜잭셔널 걸기
public class LibraryService {

	private final LibraryRepository libraryRepository;
	private final UserContextService userContextService;
	private final UserRepository userRepository;

	//라이브러리 생성
	@Transactional
	public NewLibraryResponseDto newLibrary(NewLibraryRequestDto newLibraryRequestDto) {

		//유저 정보를 꺼내고
		Long userId = userContextService.getCurrentUserId();
		Optional<User> finduser = userRepository.findById(userId);

		if (finduser.isEmpty()) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}
		User user = finduser.get();

		//들어온 라이브러리 이름 dto에서 꺼내기
		Library library = LibraryMapper.toNewLibraryEntity(newLibraryRequestDto, user);
		libraryRepository.save(library);
		NewLibraryResponseDto responseDto = LibraryMapper.fromNewLibraryResponseDto(library.getId(),
			library.getLibraryName(),
			user.getId());

		return responseDto;
	}

	//라이브러리 전체 조회
	@Transactional(readOnly = true)
	public LibraryListResponseDto getAllLibraries(Pageable pageable) {
		Long userId = userContextService.getCurrentUserId();
		Optional<User> finduser = userRepository.findById(userId);

		if (finduser.isEmpty()) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}
		Page<Library> libraries = libraryRepository.findAllByUserId(userId, pageable);
		LibraryListResponseDto libraryListResponseDto = LibraryMapper.fromLibraryListResponseDto(libraries);
		return libraryListResponseDto;
	}

	//라이브러리 삭제
	@Transactional
	public void deleteLibrary(Long libraryId) {
		Long userId = userContextService.getCurrentUserId();
		Optional<User> finduser = userRepository.findById(userId);

		if (finduser.isEmpty()) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND); //404
		}

		Library library = libraryRepository.findByIdAndUserId(libraryId, userId);
		libraryRepository.delete(library);
	}

	//라이브러리 이름 수정
	@Transactional
	public UpdateLibraryResponseDto updateLibrary(Long libraryId,
		UpdateLibraryRequestDto updateLibraryRequestDto) {
		//param으로 들어온 id, body로 들어온 변경된 라이브러리 Name
		Long userId = userContextService.getCurrentUserId();
		Optional<User> finduser = userRepository.findById(userId);

		if (finduser.isEmpty()) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}

		//바꿀 라이브러리를 가져온다
		Library searchlibrary = libraryRepository.findByIdAndUserId(libraryId, userId);
		//새로 바꿔줄 이름
		String libraryName = LibraryMapper.toLibraryName(updateLibraryRequestDto);

		//가져온 라이브러리에 이름 변경
		Library library = searchlibrary.changeLibraryName(libraryName);
		libraryRepository.save(library);
		UpdateLibraryResponseDto responseDto = LibraryMapper.fromUpdateLibraryResponseDto(library.getId(),
			library.getLibraryName());

		return responseDto;
	}
}
