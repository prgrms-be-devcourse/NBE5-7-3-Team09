package ninegle.Readio.book.dto.author;

import lombok.Builder;

@Builder
public record AuthorDto(long id, String name) {}
