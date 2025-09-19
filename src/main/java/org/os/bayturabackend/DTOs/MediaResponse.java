package org.os.bayturabackend.DTOs;

import lombok.Data;

@Data
public class MediaResponse {
    private Long id;
    private String url;
    private String altName;
    private String publicId;
}
