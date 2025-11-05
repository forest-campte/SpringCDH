package com.Campmate.DYCampmate.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor // ◀ (추가) final 필드 주입을 위해
public class FileStorageService {

    // ◀ Path fileStorageLocation 삭제

    // === S3 주입 ===
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일을 S3에 업로드하고 URL을 반환합니다.
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String storedFileName = UUID.randomUUID().toString() + extension;

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // S3에 업로드
            amazonS3Client.putObject(new PutObjectRequest(bucket, storedFileName, inputStream, metadata)
                    // ⭐️ [필수] 업로드된 파일을 공개적으로 읽을 수 있도록 ACL 설정
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            // 업로드된 파일의 S3 URL 반환
            return amazonS3Client.getUrl(bucket, storedFileName).toString();

        } catch (IOException ex) {
            throw new RuntimeException("파일 저장에 실패했습니다. " + storedFileName, ex);
        }
    }

    /**
     * S3에서 파일을 삭제합니다.
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        try {
            // S3 URL에서 파일 키(파일 이름) 추출
            // (URL 예: https://dy-campmate-storage-2025.s3.ap-northeast-2.amazonaws.com/uuid.jpg)
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // S3에서 객체(파일) 삭제
            amazonS3Client.deleteObject(bucket, fileName);

        } catch (Exception e) {
            System.err.println("S3 파일 삭제 실패: " + fileUrl + ", " + e.getMessage());
        }
    }
}
