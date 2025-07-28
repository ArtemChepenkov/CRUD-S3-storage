package ru.chepenkov.storage.gRPC.Server;

import io.grpc.stub.StreamObserver;
import ru.chepenkov.generated.FileChunk;
import ru.chepenkov.generated.FileUploadResponse;
import ru.chepenkov.generated.StorageServiceGrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ServiceGrpcServer extends StorageServiceGrpc.StorageServiceImplBase {

    @Override
    public StreamObserver<FileChunk> uploadFile(StreamObserver<FileUploadResponse> responseObserver) {
        return new StreamObserver<>() {

            OutputStream outputStream;
            Path filePath;

            {
                try {
                    filePath = Path.of("uploaded_file.bin");
                    outputStream = Files.newOutputStream(filePath,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) {
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onNext(FileChunk chunk) {
                try {
                    System.out.println("Received chunk: " + chunk.getChunkData().size() + " bytes");
                    outputStream.write(chunk.getChunkData().toByteArray());
                } catch (IOException e) {
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Upload failed: " + t.getMessage());
                try {
                    if (outputStream != null) outputStream.close();
                } catch (IOException ignored) {}
            }

            @Override
            public void onCompleted() {
                try {
                    outputStream.close();
                    System.out.println("✅ File saved to: " + filePath.toAbsolutePath());
                } catch (IOException e) {
                    responseObserver.onError(e);
                    return;
                }

                FileUploadResponse response = FileUploadResponse.newBuilder()
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }
}

