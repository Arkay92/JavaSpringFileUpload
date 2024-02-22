import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@SpringBootApplication
@RestController
@EnableAsync
public class ChunkedFileUploadApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkedFileUploadApplication.class);

    @Value("${app.tempDir:temp/}")
    private String tempDirPath;

    @Value("${app.uploadDir:uploads/}")
    private String uploadDirPath;

    public static void main(String[] args) {
        SpringApplication.run(ChunkedFileUploadApplication.class, args);
    }

    @PostMapping("/uploadChunk")
    public String uploadChunk(
            @RequestParam("file") MultipartFile fileChunk,
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("identifier") String identifier) {

        // Generate a unique file identifier
        String uniqueIdentifier = UUID.randomUUID().toString() + "-" + identifier;

        // Validate file name
        String originalFileName = fileChunk.getOriginalFilename();
        if (originalFileName == null || originalFileName.contains("..")) {
            return "Invalid file name";
        }

        File tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        String chunkFileName = tempDirPath + uniqueIdentifier + "-" + chunkNumber;
        File chunkFile = new File(chunkFileName);

        try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
            fos.write(fileChunk.getBytes());
            LOGGER.info("Chunk {} of {} uploaded successfully for file {}", chunkNumber, totalChunks, originalFileName);

            if (chunkNumber == totalChunks) {
                // Last chunk received, start reassembling
                return reassembleFile(uniqueIdentifier, totalChunks, tempDirPath, originalFileName);
            }

            return "Chunk " + chunkNumber + " of " + totalChunks + " uploaded successfully";
        } catch (IOException e) {
            LOGGER.error("Could not upload chunk {} for file {}: {}", chunkNumber, originalFileName, e.getMessage());
            return "Could not upload chunk " + chunkNumber + ": " + e.getMessage();
        }
    }

    private String reassembleFile(String uniqueIdentifier, int totalChunks, String tempDirPath, String originalFileName) {
        String safeFileName = FilenameUtils.getName(originalFileName); // Avoid directory traversal
        String assembledFilePath = uploadDirPath + safeFileName;
        File assembledFile = new File(assembledFilePath);

        try (FileOutputStream fos = new FileOutputStream(assembledFile, true)) {
            for (int i = 1; i <= totalChunks; i++) {
                Path chunkPath = Paths.get(tempDirPath + uniqueIdentifier + "-" + i);
                byte[] chunkData = Files.readAllBytes(chunkPath);
                fos.write(chunkData);
                Files.delete(chunkPath); // Delete chunk after adding it to the assembled file
            }
            LOGGER.info("File reassembled successfully: {}", assembledFilePath);
        } catch (IOException e) {
            LOGGER.error("Could not reassemble file {}: {}", assembledFilePath, e.getMessage());
            return "Could not reassemble file: " + e.getMessage();
        }

        return "File reassembled successfully: " + assembledFilePath;
    }
}
