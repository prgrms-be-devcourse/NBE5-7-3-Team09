package ninegle.Readio.book.domain

import jakarta.persistence.*
import ninegle.Readio.book.dto.BookRequestDto
import ninegle.Readio.category.domain.Category
import ninegle.Readio.publisher.domain.Publisher
import org.hibernate.annotations.SQLDelete
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@SQLDelete(sql = "UPDATE book SET expired = true, expired_at= CURRENT_TIMESTAMP WHERE id = ?")
class Book(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:Column(length = 255)
    var name: String,

    @field:Column(length = 1000)
    var description: String,

    var image: String,

    @field:Column(length = 20, unique = true)
    var isbn: String?,

    @field:Column(length = 50, unique = true)
    var ecn: String?,

    var pubDate: LocalDate,

    var updatedAt: LocalDate? = null,

    var rating: BigDecimal = BigDecimal.ZERO,

    var expired: Boolean = false,

    val expiredAt: LocalDateTime? = null,

    @field:ManyToOne
    @field:JoinColumn(name = "author_id")
    var author: Author,

    @field:ManyToOne
    @field:JoinColumn(name = "publisher_id")
    var publisher: Publisher,

    @field:ManyToOne
    @field:JoinColumn(name = "category_id")
    var category: Category
) {

    fun updateRating(rating: BigDecimal) {
        this.rating = rating
    }

    fun update(dto: BookRequestDto, category: Category, author: Author, publisher: Publisher, imageUrl: String): Book {
        this.name = dto.name
        this.description = dto.description
        this.image = imageUrl
        this.isbn = dto.isbn
        this.ecn = dto.ecn.ifEmpty { null }
        this.pubDate = dto.pubDate
        this.updatedAt = LocalDate.now()
        this.category = category
        this.author = author
        this.publisher = publisher

        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        if (id != other.id) return false
        if (expired != other.expired) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (image != other.image) return false
        if (isbn != other.isbn) return false
        if (ecn != other.ecn) return false
        if (pubDate != other.pubDate) return false
        if (updatedAt != other.updatedAt) return false
        if (rating != other.rating) return false
        if (expiredAt != other.expiredAt) return false
        if (author != other.author) return false
        if (publisher != other.publisher) return false
        if (category != other.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + expired.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + (isbn?.hashCode() ?: 0)
        result = 31 * result + (ecn?.hashCode() ?: 0)
        result = 31 * result + pubDate.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        result = 31 * result + rating.hashCode()
        result = 31 * result + (expiredAt?.hashCode() ?: 0)
        result = 31 * result + author.hashCode()
        result = 31 * result + publisher.hashCode()
        result = 31 * result + category.hashCode()
        return result
    }

}

