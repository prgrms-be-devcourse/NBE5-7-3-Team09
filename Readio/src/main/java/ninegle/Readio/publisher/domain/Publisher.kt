package ninegle.Readio.publisher.domain

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor

/**
 * create date:    25. 5. 9.
 * last update:    25. 5. 9.
 * author:  gigol
 * purpose:
 */

@Entity
class Publisher(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String
)

