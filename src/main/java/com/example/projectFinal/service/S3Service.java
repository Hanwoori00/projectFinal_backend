package com.example.projectFinal.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    /* 1. 파일 업로드 */
    public String upload(MultipartFile multipartFile) throws IOException {
        // 파일 원본 이름 가져오기
        String originalFilename = multipartFile.getOriginalFilename();
        // 확장자 포함하여 새 파일명 생성 (예: directoryPath/UUID.확장자)
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        String s3FileName = UUID.randomUUID().toString() + extension;
        String contentType = multipartFile.getContentType();

        // 메타데이터 생성
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentType(contentType);
        objMeta.setContentLength(multipartFile.getInputStream().available());

        // S3에 객체 등록
        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta);

        // 디코딩 없이, 등록된 객체의 URL 반환
        String fileUrl = amazonS3.getUrl(bucket, s3FileName).toString();

        // 디버그 용도로 콘솔에 URL 출력 (운영 환경에서는 제거)
        System.out.println(fileUrl);

        return fileUrl;
    }


    /* 2. 파일 삭제 */
    public void delete (String keyName) {
        try {
            // deleteObject(버킷명, 키값)으로 객체 삭제
            amazonS3.deleteObject(bucket, keyName);
        } catch (AmazonServiceException e) {
            log.error(e.toString());
        }
    }
}
