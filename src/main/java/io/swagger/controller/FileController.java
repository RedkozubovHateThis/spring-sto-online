package io.swagger.controller;

import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ChatMessageRepository;
import io.swagger.postgres.repository.UploadFileRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.UploadFileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@RequestMapping("/secured/files")
@RestController
public class FileController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UploadFileRepository uploadFileRepository;

    @Value("${files.catalog}")
    private String fileCatalog;

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        try {

            String uuid = UUID.randomUUID().toString();
            String fullPath = String.format( "%s%s", fileCatalog, uuid );

            file.transferTo( new File(fullPath) );

            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            Long size = file.getSize();
            Date uploadDate = new Date();

            UploadFile uploadFile = new UploadFile();
            uploadFile.setContentType( contentType );
            uploadFile.setFileName( fileName );
            uploadFile.setSize( size );
            uploadFile.setUploadDate( uploadDate );
            uploadFile.setUuid( uuid );
            uploadFile.setUploadUser( currentUser );

            uploadFileRepository.save( uploadFile );

            return ResponseEntity.ok( new UploadFileResponse( uploadFile ) );

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка загрузки файла!");
        }

    }

    @GetMapping(value = "/{uuid}/preview")
    public ResponseEntity getImagePreview(@PathVariable("uuid") String uuid) {

        UploadFile uploadFile = uploadFileRepository.findOneByUuid(uuid);
        if ( uploadFile == null ) return ResponseEntity.status(404).build();

        String fullPath = String.format( "%s%s", fileCatalog, uuid );

        try {
            BufferedImage image = ImageIO.read( new File( fullPath ) );

            if ( image == null ) return ResponseEntity.status(500).build();

            BufferedImage thumbnail = createThumb( image, 250, 250 );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "png", baos);
            byte[] imageData = baos.toByteArray();

            return ResponseEntity.ok()
                    .contentType( getMediaType( uploadFile.getContentType() ) )
                    .body( imageData );
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }

    }

    @GetMapping(value = "/{uuid}/download")
    public ResponseEntity downloadFile(@PathVariable("uuid") String uuid) {

        UploadFile uploadFile = uploadFileRepository.findOneByUuid(uuid);
        if ( uploadFile == null ) return ResponseEntity.status(404).build();

        String fullPath = String.format( "%s%s", fileCatalog, uuid );

        try {

            File file = new File( fullPath );

            if ( !file.exists() ) return ResponseEntity.status(404).build();

            byte[] resource = Files.readAllBytes( file.toPath() );

            return ResponseEntity.ok()
                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment" )
                    .contentType( getMediaType( uploadFile.getContentType() ) )
                    .contentLength( uploadFile.getSize() )
                    .body( resource );
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }

    }

    private BufferedImage createThumb(BufferedImage in, int w, int h) {
        double outputAspect = 1.0 * w / h;
        double inputAspect = 1.0 * in.getWidth() / in.getHeight();

        if ( outputAspect < inputAspect ) {
            h = (int) ( w / inputAspect );
        } else {
            w = (int) ( h * inputAspect );
        }

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(in, 0, 0, w, h, null);
        g2.dispose();
        return bi;
    }

    private MediaType getMediaType(String contentType) {

        MediaType mediaType;

        try {
            mediaType = MediaType.valueOf( contentType );
        }
        catch(InvalidMediaTypeException imde) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return mediaType;

    }

}
