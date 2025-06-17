package ninegle.Readio.subscription.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
open class Subscription(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    val userId: Long,

    var subDate: LocalDate,

    var expDate: LocalDate,

    @Column(nullable = false)
    var canceled: Boolean = false
) {
    //기본생성자
    constructor() : this(
        id = null,
        userId = 0L,
        subDate = LocalDate.now(),
        expDate = LocalDate.now(),
        canceled = false
    )

   fun updatePeriod(subDate: LocalDate, expDate: LocalDate) {
        this.subDate = subDate
        this.expDate = expDate
    }

    fun cancel() {
        this.canceled = true
    }

    fun uncancel() {
        this.canceled = false
    }

    fun isActive(): Boolean =
        expDate.isAfter(LocalDate.now()) || expDate.isEqual(LocalDate.now())

    // equals
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Subscription) return false
        return id != null && id == other.id
    }

    //hashCode
    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String {
        return "Subscription(id=$id, userId=$userId, subDate=$subDate, expDate=$expDate, canceled=$canceled)"
    }

    //새 구독 생성
    companion object {
        fun create(userId: Long, subDate: LocalDate, expDate: LocalDate): Subscription {
            return Subscription(userId = userId, subDate = subDate, expDate = expDate)
        }
    }


}