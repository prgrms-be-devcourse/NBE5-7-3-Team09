package ninegle.Readio.book.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import ninegle.Readio.book.util.*
import ninegle.Readio.global.util.genCategory
import ninegle.Readio.global.util.genPublisher
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.assertj.core.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*


private val log = KotlinLogging.logger{}
@SpringBootTest
class ReviewRepositoryTests @Autowired constructor(
    var repository: ReviewRepository
){

    @Test
    fun `repository insertion test`(){
        log.info{repository}

        assertThat(repository).isNotNull
    }

    @Test
    fun `유저 아이디로 리뷰를 찾는다`(){

//        given
        val mockEpubFile =
            genMockMultipartFile("EpubFile", "test.epub", "application/epub+zip", "test".toByteArray())

        val mockImageFile = genMockMultipartFile("ImageFile", "test.jpg", "image/jpeg", "test".toByteArray())

        val conflictEcn = UUID.randomUUID().toString()

        val request = genBookReq(
            name = "test",
            description = "책 설명",
            image = mockImageFile,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = "형이상학",
            publisherName = "한빛미디어",
            authorName = "김작가"
        )

        val expectedCategory = genCategory(110, "철학", request.categorySub)
        val expectedPublisher = genPublisher(1L, request.publisherName)
        val expectedAuthor = genAuthor(1L, request.authorName)


        val expectedEpubFileKey = "epub/${request.name}.epub"
        val expectedImageFileKey = "image/${request.name}.jpg"

        val expectedBook = genBook(null,request, expectedPublisher, expectedAuthor, expectedCategory, expectedImageFileKey)
        val expectedUser = genUser(
            email = "test@test.com",
            password = "1111",
            nickname = "test",
            phoneNumber = "01033333333"
        )

        val review = genReview(
            rating = BigDecimal(4.0),
            text = "good book",
            user = expectedUser,
            book = expectedBook
        )
//        when

//        then

    }
}