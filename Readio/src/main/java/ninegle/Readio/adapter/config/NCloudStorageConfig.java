package ninegle.Readio.adapter.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Readio - S3Config
 * create date:    25. 5. 14.
 * last update:    25. 5. 14.
 * author:  gigol
 * purpose: 
 */
@Configuration
public class NCloudStorageConfig {

	@Getter
	@Value("${cloud.ncp.s3.endpoint}")
	private String endpoint;

	@Getter
	@Value("${cloud.ncp.s3.bucket}")
	private String bucketName;

	@Value("${cloud.ncp.s3.region}")
	private String region;

	@Value("${cloud.ncp.s3.access-key}")
	private String accessKey;

	@Value("${cloud.ncp.s3.secret-key}")
	private String secretKey;

	@Bean
	public S3Client ncloudS3Client() {
		return S3Client.builder()
			.endpointOverride(URI.create(endpoint))
			.region(Region.of(region))
			.credentialsProvider(
				StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
			)
			.build();
	}

	public String toImageUrl(String image) {
		return getEndpoint()+ "/" + getBucketName() + "/" + image;
	}

}
