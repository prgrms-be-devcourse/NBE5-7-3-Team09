package ninegle.Readio.user.repository

import ninegle.Readio.user.domain.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : JpaRepository<RefreshToken, Long> {

    fun findTop1ByUserIdOrderByIdDesc(adminId: Long?): RefreshToken?

    fun findAllByUserId(adminId: Long): List<RefreshToken>
}
