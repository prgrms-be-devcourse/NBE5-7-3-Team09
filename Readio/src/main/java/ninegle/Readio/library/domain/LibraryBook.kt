package ninegle.Readio.library.domain

import jakarta.persistence.*
import ninegle.Readio.book.domain.Book

@Entity
//중간 테이블
class LibraryBook(
    @JoinColumn(name = "book_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var book: Book,


    @JoinColumn(name = "library_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var library: Library){


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

}
