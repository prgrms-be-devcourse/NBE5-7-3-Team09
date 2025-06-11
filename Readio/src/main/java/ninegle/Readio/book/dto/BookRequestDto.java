package ninegle.Readio.book.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookRequestDto {

	@NotBlank(message = "카테고리를 선택해주세요.")
	private String categorySub;

	@NotBlank(message = "출판사를 입력해주세요.")
	private String publisherName;
	@NotBlank(message = "작가를 입력해주세요.")
	private String authorName;

	@NotBlank(message = "책 제목을 입력해주세요.")
	private String name;
	private String description;

	@NotNull(message = "이미지 파일은 필수입니다.")
	private MultipartFile image;

	@NotBlank(message = "ISBN을 입력해주세요.")
	private String isbn;
	private String ecn;

	@NotNull(message = "출판일자를 입력해주세요.")
	private LocalDate pubDate;

	@NotNull(message = "EPUB 파일은 필수입니다.")
	private MultipartFile epubFile;
}
