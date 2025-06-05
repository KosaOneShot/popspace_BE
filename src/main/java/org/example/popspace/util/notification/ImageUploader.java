package org.example.popspace.util.notification;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class ImageUploader {

    private final String uploadDir = "src/main/resources/static/uploads/";

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("업로드된 파일이 비어 있습니다.");
        }

        String folder = new SimpleDateFormat("yyyyMM").format(new Date());
        String uploadPath = uploadDir + folder;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String ext = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "." + ext;
        String fullPath = Paths.get(uploadPath, fileName).toAbsolutePath().toString();

        System.out.println(">> 저장 경로: " + fullPath);

        try {
            file.transferTo(new File(fullPath));
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
            throw e;
        }

        return "/uploads/" + folder + "/" + fileName;
    }


    private String getFileExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return (idx > 0) ? fileName.substring(idx + 1) : "";
    }
}