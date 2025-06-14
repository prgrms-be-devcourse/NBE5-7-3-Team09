package ninegle.Readio.book.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import ninegle.Readio.book.dto.BookRequestDto
import ninegle.Readio.category.domain.Category
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.math.BigDecimal
import java.util.*

/**
 * Readio - Book
 * create date:    25. 5. 8.
 * last update:    25. 5. 8.
 * author:  gigol
 * purpose:
 */
@Document(indexName = "books")
class BookSearch(
    @Id
    @Field(type = FieldType.Keyword)
    val id: Long? = null,

    @Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
    var name: String,

    @Field(type = FieldType.Text)
    var image: String,

    @Field(type = FieldType.Keyword) // 정확한 매칭용
    var categoryMajor: String,

    @Field(type = FieldType.Text)
    var categorySub: String,

    @Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
    var author: String,

    @Field(type = FieldType.Double)
    var rating: BigDecimal,

    @Field(type = FieldType.Boolean)
    var expired: Boolean,
) {

    fun softDelete() {
        this.expired = true
    }

    fun update(dto: BookRequestDto, category: Category, author: Author, imageUrl: String): BookSearch {
        this.name = dto.name
        this.image = imageUrl
        this.categoryMajor = category.major
        this.categorySub = category.sub
        this.author = author.name

        return this
    }

    fun updateRating(rating: BigDecimal) {
        this.rating = rating
    }

    // 중복 제거를 위해 Override
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as BookSearch
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
