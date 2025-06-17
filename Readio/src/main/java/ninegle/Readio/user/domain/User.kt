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


//    fun getId(): Long {
//        return id ?: throw IllegalStateException("User ID는 null일 수 없습니다.")
//    }

}