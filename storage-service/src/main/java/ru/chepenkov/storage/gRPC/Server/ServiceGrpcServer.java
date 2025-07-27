package ru.chepenkov.storage.gRPC.Server;

import io.grpc.stub.StreamObserver;
import ru.chepenkov.generated.FileChunk;
import ru.chepenkov.generated.FileUploadResponse;
import ru.chepenkov.generated.StorageServiceGrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServiceGrpcServer extends StorageServiceGrpc.StorageServiceImplBase {

    @Override
    public StreamObserver<FileChunk> uploadFile(StreamObserver<FileUploadResponse> responseObserver) {
        return new StreamObserver<FileChunk>() {
            private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            @Override
            public void onNext(FileChunk fileChunk) {
                try {
                    buffer.write(fileChunk.getChunkData().toByteArray());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                byte[] resultArray = buffer.toByteArray();

                System.out.println("received file size: " + resultArray.length);

                FileUploadResponse response = FileUploadResponse.newBuilder()
                        .setIsSuccess(true)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }
}

