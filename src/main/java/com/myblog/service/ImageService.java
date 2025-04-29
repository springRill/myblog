package com.myblog.service;

import com.myblog.model.Post;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageService {

    private final ResourceLoader resourceLoader;
    private final PostService postService;

    public ImageService(ResourceLoader resourceLoader, PostService postService) {
        this.resourceLoader = resourceLoader;
        this.postService = postService;
    }

    public Resource getImage(Long id){
        Post post = postService.getPostById(id);
        String filename = post.getImagePath();
        return resourceLoader.getResource("file:images/" + filename);
    }

    public String uploadImage(MultipartFile image){
        if(image.getSize()==0){
            return null;
        }

        Resource uploadResource = resourceLoader.getResource("file:images/");
        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
//        String filename = image.getOriginalFilename();
        try {
            File uploadDir = uploadResource.getFile();
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            Path path = uploadDir.toPath().resolve(filename);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filename;
    }
}
