package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImp implements FileService{
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        //Logic to upload the image to the server and return the image URL
        //You can use any file storage service like AWS S3, Google Cloud Storage, etc. to store the images
        //For simplicity, we will just return the file name of the uploaded image
        String originalFileName = file.getOriginalFilename();
        //Generate a unique file name for the uploaded image
        String randomId = UUID.randomUUID().toString();
        //mat.jpg---->1234---->1234.jpg
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;
        File folder = new File(path); //images/ folder ka File object banaya
        if (!folder.exists()) {
            folder.mkdir();
        } //Agar folder exist nahi karta → new folder create kar do
        Files.copy(file.getInputStream(), Paths.get(filePath));//Image ko folder me copy kar diya
        return fileName;
    }
}
