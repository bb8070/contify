package com.example.contify.domain.content.service;

import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.file.FileValidator;
import com.example.contify.domain.file.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class ContentThumbnailService {
    private  final ContentRepository contentRepository;
    private final FileValidator fileValidator;
    private final S3Uploader s3Uploader;

    public String uploadThumbnail(Long userId, Long contentId, MultipartFile file) throws Exception{

        Content content = contentRepository.findById(contentId).orElseThrow(()-> new IllegalArgumentException("content not found"));

        if(!content.getCreatedUser().getId().equals(userId)) throw new AccessDeniedException("no permission");

        fileValidator.validateImage(file);

        S3Uploader.UploadResult uploaded = s3Uploader.uploadThumbnail(contentId, file);

        String oldKey = content.getThumbnailKey();
        content.changeThumbnail(uploaded.key(), uploaded.url());

        s3Uploader.delete(oldKey);
        return uploaded.url();

    }

}
