package librarian.server.image_upload.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController()
@RequestMapping("/api/upload")
public class Upload {
    
    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.key}")
    private String supabaseKey;
    
    @Value("${supabase.bucket}")
    private String supabaseBucket;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "파일이 비어있습니다.");
                return ResponseEntity.badRequest().body(error);
            }
            
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + supabaseBucket + "/" + fileName;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                uploadUrl, 
                HttpMethod.POST, 
                requestEntity, 
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String publicUrl = supabaseUrl + "/storage/v1/object/public/" + supabaseBucket + "/" + fileName;
                
                Map<String, String> result = new HashMap<>();
                result.put("message", "파일 업로드 성공");
                result.put("fileName", fileName);
                result.put("url", publicUrl);
                
                return ResponseEntity.ok(result);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "파일 업로드 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
            
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
