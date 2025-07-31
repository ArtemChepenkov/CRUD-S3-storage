package ru.chepenkov.storage.gRPC.Client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import ru.chepenkov.generated.FileChunk;
import ru.chepenkov.generated.FileDownloadRequest;
import ru.chepenkov.generated.FileUploadResponse;
import ru.chepenkov.generated.StorageServiceGrpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;

@Component
public class ServiceGrpcClient {

    private final StorageServiceGrpc.StorageServiceStub asyncStub;
    private final StorageServiceGrpc.StorageServiceBlockingStub blockingStub;

    public ServiceGrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("storage-service", 9090)
                .usePlaintext()
                .build();
        asyncStub = StorageServiceGrpc.newStub(channel);
        blockingStub = StorageServiceGrpc.newBlockingStub(channel);
        System.out.println("Success connection");
    }

    public void sendFile(InputStream inputStream, String filename, String userId) {
        StreamObserver<FileUploadResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(FileUploadResponse value) {
                System.out.println("Response: " + value);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage()+" "+t.getCause());
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
                FileChunk chunk = FileChunk.newBuilder().setChunkData(byteString)
                        .setFilename(filename)
                        .setUserId(userId)
                        .build();
                requestObserver.onNext(chunk);
            }
            requestObserver.onCompleted();
        } catch (IOException e) {
            requestObserver.onError(e);
        }
    }

    public void downloadFile(String userId, String filename, OutputStream outputStream) {
        FileDownloadRequest request = FileDownloadRequest.newBuilder()
                .setUserId(userId)
                .setFilename(filename)
                .build();

        try {
            Iterator<FileChunk> chunks = blockingStub.downloadFile(request);
            while (chunks.hasNext()) {
                FileChunk chunk = chunks.next();
                outputStream.write(chunk.getChunkData().toByteArray());
            }
            outputStream.close();
            System.out.println("✅ File downloaded successfully.");
        } catch (Exception e) {
            System.err.println("❌ Failed to download file: " + e.getMessage());
        }
    }
}
