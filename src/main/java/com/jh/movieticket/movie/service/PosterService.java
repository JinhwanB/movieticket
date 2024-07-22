package com.jh.movieticket.movie.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jh.movieticket.movie.exception.PosterErrorCode;
import com.jh.movieticket.movie.exception.PosterException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
    public Map<String, String> upload(MultipartFile image) {

        String imageName = UUID.randomUUID() + image.getOriginalFilename(); // s3에 저장할 이미지 이름
        String imageUrl = uploadToS3(image, imageName);

        Map<String, String> result = new HashMap<>();
        result.put("imageName", imageName);
        result.put("imageUrl", imageUrl);

        return result;
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
     * @param image 저장할 이미지
     * @param imageName  이미지 이름
     * @return 저장된 이미지 링크
     */
    private String uploadToS3(MultipartFile image, String imageName) {

        try(InputStream inputStream = image.getInputStream()){
            amazonS3Client.putObject(new PutObjectRequest(bucket, imageName, inputStream, null).withCannedAcl(
                CannedAccessControlList.PublicRead));
        }catch (IOException e){
            throw new PosterException(PosterErrorCode.FAIL_UPLOAD_IMAGE);
        }

        return amazonS3Client.getUrl(bucket, imageName).toString();
    }
}
