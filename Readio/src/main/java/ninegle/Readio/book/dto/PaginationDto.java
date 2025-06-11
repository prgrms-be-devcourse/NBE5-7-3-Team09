package ninegle.Readio.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Readio - PaginationDto
 * create date:    25. 5. 12.
 * last update:    25. 5. 12.
 * author:  gigol
 * purpose: 
 */
@Builder
public record PaginationDto(long totalElements, int totalPages, int currentPage, int size) {
}