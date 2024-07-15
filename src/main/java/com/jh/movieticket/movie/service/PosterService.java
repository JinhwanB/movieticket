package com.jh.movieticket.movie.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jh.movieticket.movie.exception.PosterErrorCode;
import com.jh.movieticket.movie.exception.PosterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosterService {

    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    /**
     * 이미지를 s3에 저장하고 로컬에 저장된 이미지를 제거
     *
     * @param image 저장할 이미지
     * @return s3에 저장된 이미지 링크
     */
    public String upload(MultipartFile image) {
        File uploadFile = convertToFile(image)
            .orElseThrow(() -> new PosterException(PosterErrorCode.FAIL_CONVERT_TO_FILE));

        String imageName = UUID.randomUUID() + uploadFile.getName(); // s3에 저장할 이미지 이름
        String imageUrl = uploadToS3(uploadFile, imageName);

        if (!uploadFile.delete()) { // 로컬에 저장되어 있는 이미지 제거
            log.info("로컬에 저장한 이미지를 삭제 실패했습니다. 이미지 이름 : {}", uploadFile.getName());
        }

        return imageUrl;
    }

    /**
     * s3에 저장된 이미지 삭제
     *
     * @param fileName 이미지 파일 이름
     */
    public void deleteImage(String fileName) {

        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new PosterException(PosterErrorCode.FAIL_DELETE_IMAGE);
        }
    }

    /**
     * s3에 이미지를 저장
     *
     * @param uploadFile 저장할 이미지
     * @param imageName  이미지 이름
     * @return 저장된 이미지 링크
     */
    private String uploadToS3(File uploadFile, String imageName) {

        amazonS3Client.putObject(new PutObjectRequest(bucket, imageName, uploadFile).withCannedAcl(
            CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, imageName).toString();
    }

    /**
     * 이미지를 로컬에 저장
     *
     * @param image 입력받은 이미지
     * @return 로컬에 저장한 이미지
     */
    private Optional<File> convertToFile(MultipartFile image) {

        File convertFile = new File(
            System.getProperty("user.dir") + "/" + image.getOriginalFilename());
        try {
            // 이미지를 위 위치에 저장
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(image.getBytes());
                }

                return Optional.of(convertFile);
            }
        } catch (IOException e) {
            throw new PosterException(PosterErrorCode.FAIL_CONVERT_TO_FILE);
        }

        return Optional.empty();
    }
}
