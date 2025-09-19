package org.os.bayturabackend.DTOs;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MediaRequest {
    private MultipartFile file;
    private String altName;
}

