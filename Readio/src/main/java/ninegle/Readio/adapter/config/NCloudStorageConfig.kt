package ninegle.Readio.adapter.config

import ninegle.Readio.adapter.util.NCloudStorageUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

/**
 * Readio - S3Config
 * create date:    25. 5. 14.
 * last update:    25. 5. 14.
 * author:  gigol
 * purpose:
 */
@Configuration
class NCloudStorageConfig(
    @Value("\${cloud.ncp.s3.endpoint}")
    val endpoint: String,

    @Value("\${cloud.ncp.s3.bucket}")
    private val bucketName: String,

    @Value("\${cloud.ncp.s3.region}")
    private val region: String,

    @Value("\${cloud.ncp.s3.access-key}")
    private val accessKey: String,

    @Value("\${cloud.ncp.s3.secret-key}")
    private val secretKey: String
) {

    init {
        NCloudStorageUtils.init(endpoint, bucketName)
    }

    @Bean
    fun nCloudS3Client(): S3Client {
        return S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
            )
            .build()
    }
}
