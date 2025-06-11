package ninegle.Readio.adapter.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Readio - NCloudStorageService
 * create date:    25. 5. 14.
 * last update:    25. 5. 14.
 * author:  gigol
 * purpose: 네이버 클라우드 연동
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NCloudStorageService {
	private final S3Client s3Client;

	@Value("${cloud.ncp.s3.bucket}")
	private String bucketName;

	public void uploadFile(String key, MultipartFile file) throws IOException {
		PutObjectRequest putRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(key)
			.contentType(file.getContentType())
			.acl(ObjectCannedACL.PUBLIC_READ)
			.build();

		s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
	}

	public byte[] downloadFile(String key) {
		GetObjectRequest getRequest = GetObjectRequest.builder()
			.bucket(bucketName)
			.key(key)
			.build();

		return s3Client.getObjectAsBytes(getRequest).asByteArray();
	}

	public boolean fileExists(String key) {
		try {
			HeadObjectRequest headRequest = HeadObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
			s3Client.headObject(headRequest);
			return true;
		} catch (S3Exception e) {
			return false;
		}
	}

	public void renameFileOnCloud(String beforeName, String afterName, String folderName, String extension ) {
		String oldKey = generateObjectKey(beforeName, folderName, extension);
		String newKey = generateObjectKey(afterName, folderName, extension);

		// 이름이 동일하면 실행하지 않음
		if (oldKey.equals(newKey)) {
			return;
		}

		try {
			CopyObjectRequest copyReq = CopyObjectRequest.builder()
				.sourceBucket(bucketName)
				.sourceKey(oldKey)
				.destinationBucket(bucketName)
				.destinationKey(newKey)
				.acl(ObjectCannedACL.PUBLIC_READ)
				.build();
			s3Client.copyObject(copyReq);

			DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(oldKey)
				.build();
			s3Client.deleteObject(deleteReq);
		} catch (S3Exception e) {
			log.info("e.getMessage() = {}", e.getMessage());
			throw new BusinessException(ErrorCode.DUPLICATE_NAME);
		}
	}

	public void deleteFileOnCloud(String key, String folderName, String extension) {
		String fileKey = generateObjectKey(key, folderName, extension);
		try {
			DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(fileKey)
				.build();
			s3Client.deleteObject(deleteReq);
		} catch (NoSuchKeyException e) {
			log.debug("삭제하려는 키가 없습니다. key={}", key);
		} catch (S3Exception e) {
			log.error("Cloud 파일 삭제 중 오류 발생. key={}, message={}", key, e.awsErrorDetails().errorMessage());
			throw e;
		}
	}

	private String generateObjectKey(String bookName, String folderName, String extension ) {
		return folderName+ "/" + bookName + extension;
	}

	public String generateObjectUrl(String key) {
		GetUrlRequest getUrlRequest = GetUrlRequest.builder()
			.bucket(bucketName)
			.key(key)
			.build();
		return s3Client.utilities().getUrl(getUrlRequest).toString();
	}

}
