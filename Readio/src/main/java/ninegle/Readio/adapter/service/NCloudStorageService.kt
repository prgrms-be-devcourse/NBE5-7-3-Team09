package ninegle.Readio.adapter.service

import io.github.oshai.kotlinlogging.KotlinLogging
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.io.IOException

/**
 * Readio - NCloudStorageService
 * create date:    25. 5. 14.
 * last update:    25. 5. 14.
 * author:  gigol
 * purpose: 네이버 클라우드 연동
 */

@Service
class NCloudStorageService(
    private val s3Client: S3Client,

    @Value("\${cloud.ncp.s3.bucket}")
    private val bucketName: String
) {

    private val log = KotlinLogging.logger {}

    @Throws(IOException::class)
    fun uploadFile(key: String, file: MultipartFile) {
        val putRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.contentType)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build()

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()))
    }

    fun downloadFile(key: String): ByteArray {
        val getRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        return s3Client.getObjectAsBytes(getRequest).asByteArray()
    }

    fun fileExists(key: String): Boolean {
        try {
            val headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()
            s3Client.headObject(headRequest)
            return true
        } catch (e: S3Exception) {
            return false
        }
    }

    fun renameFileOnCloud(beforeName: String, afterName: String, folderName: String, extension: String) {
        val oldKey = generateObjectKey(beforeName, folderName, extension)
        val newKey = generateObjectKey(afterName, folderName, extension)

        // 이름이 동일하면 실행하지 않음
        if (oldKey == newKey) {
            return
        }

        try {
            val copyReq = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(oldKey)
                .destinationBucket(bucketName)
                .destinationKey(newKey)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build()
            s3Client.copyObject(copyReq)

            val deleteReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(oldKey)
                .build()
            s3Client.deleteObject(deleteReq)
        } catch (e: S3Exception) {
            log.info { "e.message() = ${e.message}" }
            throw BusinessException(ErrorCode.DUPLICATE_NAME)
        }
    }

    fun deleteFileOnCloud(key: String, folderName: String, extension: String) {
        val fileKey = generateObjectKey(key, folderName, extension)
        try {
            val deleteReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build()
            s3Client.deleteObject(deleteReq)
        } catch (e: NoSuchKeyException) {
            log.debug { "삭제하려는 키가 없습니다. key=${key}" }
        } catch (e: S3Exception) {
            log.error { "Cloud 파일 삭제 중 오류 발생. key=${key}, message=${e.awsErrorDetails().errorMessage()}" }
            throw e
        }
    }

    private fun generateObjectKey(bookName: String, folderName: String, extension: String): String {
        return "$folderName/$bookName$extension"
    }

    fun generateObjectUrl(key: String): String {
        val getUrlRequest = GetUrlRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()
        return s3Client.utilities().getUrl(getUrlRequest).toString()
    }
}
