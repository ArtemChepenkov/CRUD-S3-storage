package ru.chepenkov.storage.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.chepenkov.storage.gRPC.Client.ServiceGrpcClient;

import java.io.IOException;

@RestController
public class StorageController {

    private final ServiceGrpcClient serviceGrpcClient;

    public StorageController(ServiceGrpcClient serviceGrpcClient) {
        this.serviceGrpcClient = serviceGrpcClient;
    }

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            System.err.println("No file");
        }
        serviceGrpcClient.sendFile(file.getBytes());
        System.out.println("after send");
    }
}
