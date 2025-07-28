package ru.chepenkov.storage.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.chepenkov.storage.gRPC.Client.ServiceGrpcClient;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class StorageController {

    private final ServiceGrpcClient serviceGrpcClient;

    public StorageController(ServiceGrpcClient serviceGrpcClient) {
        this.serviceGrpcClient = serviceGrpcClient;
    }

    @PostMapping(path = "/upload", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void uploadFile(HttpServletRequest request) throws IOException {
        InputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        serviceGrpcClient.sendFile(inputStream);
    }

    // don't support big files
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public void uploadMultipart(@RequestParam("file") MultipartFile file) throws IOException {
//        serviceGrpcClient.sendFile(file.getBytes());
//    }
}
