package ninegle.Readio.book.domain

import jakarta.persistence.*
import ninegle.Readio.book.dto.reviewdto.ReviewRequestDto
import ninegle.Readio.user.domain.User
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class Review (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var rating: BigDecimal,

    @field:Column(
        columnDefinition = "TEXT"
    )
    var text: String,

    @field:JoinColumn(
        name = "user_id",
        nullable = false
    )
    @field:ManyToOne
    var user: User,

    @field:JoinColumn(
        name = "book_id",
        nullable = false
    )
    @field:ManyToOne
    var book: Book,

    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
) {

    @PrePersist
    fun onCreate() {
        this.createdAt = LocalDateTime.now()
        this.updatedAt = this.createdAt
    }

    @PreUpdate
    fun onUpdate() {
        this.updatedAt = LocalDateTime.now()
    }

    fun update(dto: ReviewRequestDto) : Review{
        this.rating = dto.rating
        this.text = dto.text
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Review

        if (id != other.id) return false
        if (rating != other.rating) return false
        if (text != other.text) return false
        if (user != other.user) return false
        if (book != other.book) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + rating.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + book.hashCode()
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        return result
    }
}
