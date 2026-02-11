package com.example.contify.domain.file;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class FileValidator {
    private  static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXT = Set.of("jpg","jpeg","png", "webp");
    private static final Set<String> ALLOWED_MIME = Set.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, "image/webp");

    public void validateImage(MultipartFile file) {
        if(file ==null || file.isEmpty()) throw new IllegalArgumentException("file is empty");
        if(file.getSize()>MAX_BYTES) throw new IllegalArgumentException("file too large");

        String contentType = file.getContentType();
        if(contentType == null || !ALLOWED_MIME.contains(contentType)){
            throw new IllegalArgumentException("invalid content-type");
        }
        String ext = extractExt(file.getOriginalFilename());
        if(!ALLOWED_EXT.contains(ext)) throw new IllegalArgumentException("invalid extension");

    }

    private String extractExt(String filename){
        if(filename==null) return "";
        int idx = filename.lastIndexOf('.');
        if(idx<0|| idx==filename.length()-1) return "";
        return filename.substring(idx+1).toLowerCase();
    }

}

