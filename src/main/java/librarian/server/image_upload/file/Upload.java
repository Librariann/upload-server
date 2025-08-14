package librarian.server.image_upload.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController()
@RequestMapping("/api/upload")
public class Upload {
    
    private final S3Client s3Client;
    
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    
    @Value("${cloud.aws.region.static}")
    private String region;
    
    public Upload(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "파일이 비어있습니다.");
                return ResponseEntity.badRequest().body(error);
            }
            
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            
            String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                    bucketName, region, fileName);
            
            Map<String, String> result = new HashMap<>();
            result.put("message", "파일 업로드 성공");
            result.put("fileName", fileName);
            result.put("url", publicUrl);
            
            return ResponseEntity.ok(result);
            
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "파일 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "업로드 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
