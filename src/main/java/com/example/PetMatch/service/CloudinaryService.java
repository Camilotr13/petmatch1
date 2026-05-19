package com.example.PetMatch.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("folder", "petmatch");

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        String secureUrl = (String) uploadResult.get("secure_url");

        if (secureUrl == null || secureUrl.isBlank()) {
            throw new IOException("Cloudinary no retornó una URL válida");
        }

        return secureUrl;
    }
}
