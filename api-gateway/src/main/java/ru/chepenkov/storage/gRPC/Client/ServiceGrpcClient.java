package ru.chepenkov.storage.gRPC.Client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import ru.chepenkov.generated.FileChunk;
import ru.chepenkov.generated.FileUploadResponse;
import ru.chepenkov.generated.StorageServiceGrpc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Component
public class ServiceGrpcClient {

    private final StorageServiceGrpc.StorageServiceStub asyncStub;

    public ServiceGrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("storage-service", 9090)
                .usePlaintext()
                .build();
        asyncStub = StorageServiceGrpc.newStub(channel);
        System.out.println("Success connection");
    }

    public void sendFile(InputStream inputStream) {
        StreamObserver<FileUploadResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(FileUploadResponse value) {
                System.out.println("Response: " + value);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Upload complete");
            }
        };

        StreamObserver<FileChunk> requestObserver = asyncStub.uploadFile(responseObserver);

        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                ByteString byteString = ByteString.copyFrom(buffer, 0, bytesRead);
                FileChunk chunk = FileChunk.newBuilder().setChunkData(byteString).build();
                requestObserver.onNext(chunk);
            }
            requestObserver.onCompleted();
        } catch (IOException e) {
            requestObserver.onError(e);
        }
    }
}
