package ninegle.Readio.user.domain

import jakarta.persistence.*


@Entity
class User (
    @field:Column(length = 40, nullable = false)
    var email: String,

    @field:Column(length = 100, nullable = false)
    var password: String,

     var nickname: String,

     var phoneNumber: String,


     var point: Long = 15000L ){


    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     var id: Long? = null

    @Enumerated(EnumType.STRING)
    val role = Role.USER


    // 닉네임 변경 메서드
    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    // 핸드폰번호 변경 메서드
    fun updatePhoneNumber(phone_number: String) {
        this.phoneNumber = phone_number
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (point != other.point) return false
        if (id != other.id) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (nickname != other.nickname) return false
        if (phoneNumber != other.phoneNumber) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = point.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }


//    fun getId(): Long {
//        return id ?: throw IllegalStateException("User ID는 null일 수 없습니다.")
//    }

}