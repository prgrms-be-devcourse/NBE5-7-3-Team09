package ninegle.Readio.category.repository

import ninegle.Readio.category.domain.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findBySub(sub: String): Category?
}
