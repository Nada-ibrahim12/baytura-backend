package org.os.bayturabackend.dto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MediaRequest {
    private MultipartFile file;
    private String altName;
}

