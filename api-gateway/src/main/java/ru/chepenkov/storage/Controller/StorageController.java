package ru.chepenkov.storage.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    public void uploadFile(HttpServletRequest request,
                           @RequestParam String filename,
                           @RequestParam String userId) throws IOException {
        InputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            System.err.println("Storage controller: " + e.getMessage());
            return;
        }
        serviceGrpcClient.sendFile(inputStream, filename, userId);
    }


    @GetMapping("/download")
    public void downloadFile(@RequestParam String userId,
                             @RequestParam String filename,
                             HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");


        serviceGrpcClient.downloadFile(userId, filename, response.getOutputStream());
    }
}
