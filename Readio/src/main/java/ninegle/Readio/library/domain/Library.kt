package ninegle.Readio.library.domain

import jakarta.persistence.*
import ninegle.Readio.user.domain.User
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Table(name = "`library`")
@Entity
class Library(

    @field: Column(nullable = false)
    var libraryName: String,

    @field:JoinColumn(name = "user_id")
    @field:ManyToOne (fetch = FetchType.LAZY)
    var user: User)
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    //나 자신 즉 library 삭제하면 libraryBook도 삭제
    @OneToMany(mappedBy = "library", cascade = [CascadeType.ALL], orphanRemoval = true)
    val libraryBook: List<LibraryBook> = mutableListOf()

    var createdAt: LocalDateTime = LocalDateTime.now()

    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null

    fun changeLibraryName(newLibraryName: String): Library {
        this.libraryName = newLibraryName
        return this
    }
}
