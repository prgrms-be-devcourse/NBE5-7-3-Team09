package ninegle.Readio.library.domain;

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
import ninegle.Readio.book.domain.Book;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//중간 테이블
public class LibraryBook {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;

	@ManyToOne
	@JoinColumn(name = "library_id")
	private Library library;

	@Builder
	public LibraryBook(Book book, Library library) {
		this.book = book;
		this.library = library;
	}
}
