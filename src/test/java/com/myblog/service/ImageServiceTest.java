package com.myblog.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @Value("${image.path:imagesfolder}")
    private String imagePath;

    @Test
    void uploadGetDeleteImage() throws IOException {
        Path filePath = Paths.get(imagePath);

        String originalFileName = "testImage.png";
        byte[] content = "imageContent".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("image", originalFileName, MediaType.IMAGE_PNG_VALUE, content);

        String fileName = imageService.uploadImage(multipartFile);
        assertNotNull(fileName);

        Resource resource = imageService.getImage(fileName);
        assertEquals(fileName, resource.getFile().getName());

        long fileCount = Files.list(filePath).filter(Files::isRegularFile).count();
        imageService.deleteImage(fileName);
        assertEquals(fileCount-1, Files.list(filePath).filter(Files::isRegularFile).count());
    }
}