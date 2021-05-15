package io.swagger.response;

import io.swagger.postgres.model.UploadFile;
import lombok.Data;

@Data
public class UploadFileResponse {

    private Long id;
    private String fileName;

    public UploadFileResponse(UploadFile uploadFile) {
        id = uploadFile.getId();
        fileName = uploadFile.getFileName();
    }

}
