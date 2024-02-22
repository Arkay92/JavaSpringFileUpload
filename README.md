# JavaSpringFileUpload
JavaSpringFileUpload is a Spring Boot application designed to handle large file uploads efficiently by breaking them down into smaller, manageable chunks. This approach not only improves the reliability of file transfers, especially in environments with unstable network conditions, but also provides a foundation for building robust file upload features in web applications.

## Features
- **Chunked File Upload**: Splits large files into smaller chunks for upload, reducing the risk of transfer interruptions.
- **Concurrent Uploads**: Handles multiple file uploads simultaneously with unique identifiers for each session.
- **File Reassembly**: Automatically reassembles uploaded chunks into their original file format on the server side.
- **Error Handling**: Provides detailed logging and error messages for troubleshooting upload issues.
- **Configurable**: Allows customization of temporary and final storage paths through application properties.

## Getting Started

### Prerequisites
- JDK 11 or later
- Maven 3.6 or later (for building the project)
- Spring Boot 2.5.0 or later

### Running the Application
1. Clone the repository:
```
git clone https://github.com/your-username/JavaSpringFileUpload.git
```
   
2. Navigate to the project directory:
```
cd ChunkyUploader
```

3. Build the project using Maven:
```
mvn clean install
```
4. Run the application:
```
java -jar target/chunkyuploader-0.0.1-SNAPSHOT.jar
```

## Usage
To upload a file in chunks, make POST requests to /uploadChunk with the following parameters:

file: The file chunk (part of the multipart file).
chunkNumber: The sequence number of the current chunk.
totalChunks: The total number of chunks for the file.
identifier: A unique identifier for the file being uploaded.

## Configuration
You can configure the temporary and upload directory paths in the application.properties file:

```
app.tempDir=temp/
app.uploadDir=uploads/
```

##Contributing
Contributions are welcome! Please feel free to submit a pull request or create an issue for any enhancements, bug fixes, or feature requests.

##License
This project is open-sourced under the MIT License. See the LICENSE file for more details.
