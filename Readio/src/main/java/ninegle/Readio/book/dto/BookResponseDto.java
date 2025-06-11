package ninegle.Readio.book.dto;

import java.time.LocalDate;

import lombok.Builder;
import ninegle.Readio.book.dto.author.AuthorDto;
import ninegle.Readio.category.dto.CategoryDto;
import ninegle.Readio.publisher.dto.PublisherDto;

@Builder
public record BookResponseDto(long id, String name, String description, String image, String isbn, String ecn,
							  LocalDate pubDate, CategoryDto category, PublisherDto publisher, AuthorDto author) {

}
