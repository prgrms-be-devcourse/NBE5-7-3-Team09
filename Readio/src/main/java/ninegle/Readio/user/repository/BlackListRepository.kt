package ninegle.Readio.user.repository

import ninegle.Readio.user.domain.BlackList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlackListRepository : JpaRepository<BlackList, Long> {
    fun findByInversionAccessToken(token: String): BlackList?
}
