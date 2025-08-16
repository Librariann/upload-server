package librarian.server.image_upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ImageUploadApplication {
	public static void main(String[] args) {
		SpringApplication.run(ImageUploadApplication.class, args);
	}
}
