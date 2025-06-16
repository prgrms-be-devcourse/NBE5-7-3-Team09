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
}
