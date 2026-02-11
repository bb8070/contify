package com.example.contify.domain.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;
  //  https://contify-dev-am322.s3.ap-northeast-2.amazonaws.com

    public UploadResult uploadThumbnail(Long contentId, MultipartFile file) throws IOException{
        String ext = getExt(file.getOriginalFilename());
        String key = "contify/dev/contents/"+contentId+"/thumbnails/"+ UUID.randomUUID();

        PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key).contentType(file.getContentType()).build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

//        String url =  "https://contify-dev-am322.s3.ap-northeast-2.amazonaws.com"+"/"+key;

        String url = String.valueOf(presignGetUrl(key, Duration.ofMinutes(10)));

        return new UploadResult(key, url);

    }

    public void delete(String key){
        if(key==null || key.isBlank()) return;

        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());

    }

    public URL presignGetUrl(String key , Duration ttl){
        try(S3Presigner presigner = S3Presigner.builder()
                .region(s3Client.serviceClientConfiguration().region())
                .credentialsProvider(software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.create())
                .build()
        ){
            GetObjectRequest getReq = GetObjectRequest.builder().bucket(bucket).key(key).build();
            GetObjectPresignRequest presignReq =  GetObjectPresignRequest.builder().signatureDuration(ttl).getObjectRequest(getReq).build();
            return presigner.presignGetObject(presignReq).url();
        }


    }

    private  String getExt(String fileName){
        if(fileName == null ) return "jpg";
        int idx= fileName.lastIndexOf(".");
        if(idx < 0 || idx == fileName.length()-1 ) return "jpg";
        return fileName.substring(idx+1).toLowerCase();
    }

    public record UploadResult(String key , String url){}

}
