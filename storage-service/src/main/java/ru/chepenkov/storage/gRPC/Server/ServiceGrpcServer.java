package ru.chepenkov.storage.gRPC.Server;

import io.grpc.stub.StreamObserver;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import ru.chepenkov.generated.FileChunk;
import ru.chepenkov.generated.FileDownloadRequest;
import ru.chepenkov.generated.FileUploadResponse;
import ru.chepenkov.generated.StorageServiceGrpc;
import ru.chepenkov.storage.MinioStorage.MinioStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ServiceGrpcServer extends StorageServiceGrpc.StorageServiceImplBase {

    private final MinioStorage minioStorage = new MinioStorage(
            "http://minio:9000", "minioadmin", "minioadmin");

    @Override
    public StreamObserver<FileChunk> uploadFile(StreamObserver<FileUploadResponse> responseObserver) {
        return new StreamObserver<>() {

            OutputStream outputStream;
            Path filePath;
            String userId;
            String filename;
            File tempFile;


            @Override
            public void onNext(FileChunk chunk) {
                try {
                    if (outputStream == null) {
                        userId = chunk.getUserId();
                        filename = chunk.getFilename();
                        tempFile = File.createTempFile("upload_", "_" + userId + "_" + filename);
                        outputStream = new FileOutputStream(tempFile);
                        outputStream.write(chunk.getChunkData().toByteArray());
                    }
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
                    try (InputStream inputStream = new FileInputStream(tempFile)) {

                        minioStorage.uploadToMinio(userId, filename, inputStream, tempFile.length());
                    } catch (ServerException | InsufficientDataException | ErrorResponseException |
                             NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                             XmlParserException | InternalException e) {
                        System.err.println("Upload failed at server onCompleted " + e.getMessage());;
                        e.printStackTrace();
                        responseObserver.onError(e);
                    }
                    System.out.println("✅ File saved");
                } catch (Exception e) {
                    e.printStackTrace();
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

    @Override
    public void downloadFile(FileDownloadRequest request, StreamObserver<FileChunk> responseObserver) {

        String userId = request.getUserId();
        String filename = request.getFilename();
        String bucketName = "bucket-"+userId+"-bucket";

        try (InputStream inputStream = minioStorage.downloadFromMinio(bucketName, filename)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            int chunkIndex = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                FileChunk chunk = FileChunk.newBuilder()
                        .setUserId(userId)
                        .setFilename(filename)
                        .setChunkIndex(chunkIndex++)
                        //.setIsLast(isLast)
                        .setChunkData(com.google.protobuf.ByteString.copyFrom(buffer, 0, bytesRead))
                        .build();
                responseObserver.onNext(chunk);
            }
            responseObserver.onCompleted();

        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }
}

