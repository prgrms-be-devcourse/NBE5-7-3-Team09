package ninegle.Readio.category.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor

/**
 * Readio - category
 * create date:    25. 5. 9.
 * last update:    25. 5. 9.
 * author:  gigol
 * purpose:
 */

@Entity
class Category(
    @Id
    val id: Long,

    val major: String,

    val sub: String
) {


}
