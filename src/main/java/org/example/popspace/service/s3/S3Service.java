package org.example.popspace.service.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3에 이미지 업로드 하기
     */
    public String uploadImage(MultipartFile image,String directory)  {

        try {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename(); // 고유한 파일 이름 생성
            String fullPath = directory + "/" + fileName;  // 폴더처럼 사용

            // 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());

            // S3에 파일 업로드 요청 생성
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fullPath, image.getInputStream(), metadata);

            // S3에 파일 업로드
            amazonS3Client.putObject(putObjectRequest);

            return getPublicUrl(fullPath);
        }catch (IOException e) {
            throw new CustomException(ErrorCode.S3_INVALID);
        }
    }

    private String getPublicUrl(String fullPath) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3Client.getRegionName(), fullPath);
    }
}