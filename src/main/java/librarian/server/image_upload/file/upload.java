package librarian.server.image_upload.file;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/TEST")
public class upload {
    @GetMapping("/upload")
    public int fileUpload() {
        return 123;
    }
}
