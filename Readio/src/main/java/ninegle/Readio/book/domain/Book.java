package ninegle.Readio.book.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ninegle.Readio.book.dto.BookRequestDto;
import ninegle.Readio.category.domain.Category;
import ninegle.Readio.publisher.domain.Publisher;

/**
 * Readio - Book
 * create date:    25. 5. 8.
 * last update:    25. 5. 8.
 * author:  gigol
 * purpose:
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE book SET expired = true, expired_at= CURRENT_TIMESTAMP WHERE id = ?")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(length = 1000, nullable = false)
	private String description;

	private String image;

	@Column(length = 20, unique = true)
	private String isbn;

	@Column(length = 50, unique = true)
	private String ecn;

	@Column(nullable = false)
	private LocalDate pubDate;

	private LocalDate updatedAt;

	private BigDecimal rating = BigDecimal.ZERO;

	private Boolean expired = false;

	private LocalDateTime expiredAt;

	@ManyToOne
	@JoinColumn(name = "author_id")
	private Author author;

	@ManyToOne
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	public void updateRating(BigDecimal rating) {
		this.rating = rating;
	}

	@Builder
	public Book(String name, String description, String image, String isbn, String ecn, LocalDate pubDate,
		Author author, Publisher publisher, Category category) {
		this.name = name;
		this.description = description;
		this.image = image;
		this.isbn = isbn;
		this.ecn = ecn;
		this.pubDate = pubDate;
		this.author = author;
		this.publisher = publisher;
		this.category = category;
	}

	public Book update(BookRequestDto dto, Category category, Author author, Publisher publisher, String imageUrl) {
		this.name = dto.getName();
		this.description = dto.getDescription();
		this.image = imageUrl;
		this.isbn = dto.getIsbn();
		this.ecn = dto.getEcn();
		this.pubDate = dto.getPubDate();
		this.updatedAt = LocalDate.now();
		this.category = category;
		this.author = author;
		this.publisher = publisher;

		return this;
	}

}

