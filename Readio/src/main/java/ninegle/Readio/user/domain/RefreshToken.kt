package ninegle.Readio.user.domain

import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime


@Entity
@Table(name = "refresh_token")
class RefreshToken (
    @field:Column(nullable = false)
    var refreshToken: String, //단방향으로 유저와 연결


    @field:JoinColumn(name = "user_id") @field:ManyToOne(fetch = FetchType.LAZY)
    var user: User) {

    constructor() : this("", User()) // 기본 생성자 (JPA용) }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @UpdateTimestamp
    private var UpdateTimeAt: LocalDateTime? = null

    fun newSetRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken //갱신 시간 업데이트 (옵션)
    }
}
