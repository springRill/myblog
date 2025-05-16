package com.myblog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageService {

    private final ResourceLoader resourceLoader;

    @Value("${image.path:imagesfolder}")
    private String imagePath;

    public ImageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource getImage(String filename) {
        if(filename!=null) {
            try {
                Path filePath = Paths.get(imagePath).resolve(filename);
                Resource resource = new UrlResource(filePath.toUri());
                return resource;
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return resourceLoader.getResource("classpath:images/no_image.png");
    }

    public String uploadImage(MultipartFile image){
        if(image.getSize()==0){
            return null;
        }
        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
        try {
            Path filePath = Paths.get(imagePath);
            Resource uploadResource = new UrlResource(filePath.toUri());
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

    public void deleteImage(String filename){
        try {
            Path path = Paths.get(imagePath, filename); // путь к реальной папке
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
