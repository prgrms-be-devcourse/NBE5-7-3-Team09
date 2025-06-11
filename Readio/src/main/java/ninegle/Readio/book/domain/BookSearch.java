package ninegle.Readio.book.domain;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import ninegle.Readio.book.dto.BookRequestDto;
import ninegle.Readio.category.domain.Category;

/**
 * Readio - Book
 * create date:    25. 5. 8.
 * last update:    25. 5. 8.
 * author:  gigol
 * purpose:
 */
@Getter
@Document(indexName = "books")
@Builder
public class BookSearch {

	@Id
	@Field(type = FieldType.Keyword)
	private Long id;

	@Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
	private String name;

	@Field(type = FieldType.Text)
	private String image;

	@Field(type = FieldType.Keyword) // 정확한 매칭용
	private String categoryMajor;

	@Field(type = FieldType.Text)
	private String categorySub;

	@Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
	private String author;

	@Field(type = FieldType.Double)
	private BigDecimal rating;

	@Field(type = FieldType.Boolean)
	private Boolean expired;

	public void softDelete() {
		this.expired = true;
	}

	public BookSearch update(BookRequestDto dto, Category category, Author author, String imageUrl) {
		this.name = dto.getName();
		this.image = imageUrl;
		this.categoryMajor = category.getMajor();
		this.categorySub = category.getSub();
		this.author = author.getName();

		return this;
	}

	public void updateRating(BigDecimal rating) {
		this.rating = rating;
	}

	// 중복 제거를 위해 Override
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BookSearch that = (BookSearch) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
