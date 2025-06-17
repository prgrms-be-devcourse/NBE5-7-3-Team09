package ninegle.Readio.book.domain

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import ninegle.Readio.user.domain.User

@Entity
class Preference (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

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
    var book: Book
) {
    override fun toString(): String {
        return "Preference{" +
                "id=" + id +
                ", user=" + user +
                ", book=" + book +
                '}'
    }


}
