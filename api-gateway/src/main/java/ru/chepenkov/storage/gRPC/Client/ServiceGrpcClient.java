package ru.chepenkov.storage.gRPC.Client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import ru.chepenkov.generated.FileChunk;
import ru.chepenkov.generated.FileUploadResponse;
import ru.chepenkov.generated.StorageServiceGrpc;

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

    public void sendFile(byte[] fileBytes) {
        StreamObserver<FileUploadResponse> responseStreamObserver =
                new StreamObserver<FileUploadResponse>() {
                    @Override
                    public void onNext(FileUploadResponse fileUploadResponse) {
                        System.out.println(fileUploadResponse.toString());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("done");
                    }
                };
        StreamObserver<FileChunk> requestStreamObserver =
                asyncStub.uploadFile(responseStreamObserver);

        int chunkSize = 1024;
        for (int i = 0; i < fileBytes.length; i += chunkSize) {
            int len = Math.min(chunkSize, fileBytes.length - i);
            byte[] chunk = Arrays.copyOfRange(fileBytes, i, i + len);
            requestStreamObserver.onNext(FileChunk.newBuilder().setChunkData(ByteString.copyFrom(chunk)).build());
        }

        requestStreamObserver.onCompleted();
    }

}
