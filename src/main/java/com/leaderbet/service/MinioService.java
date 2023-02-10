package com.leaderbet.service;

import io.minio.*;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
@AllArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public byte[] getObject(String bucket, String path) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                log.error("Bucket of name '{}' does not exist!", bucket);
                return null;
            }

            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build())) {
                return stream.readAllBytes();
            }

        } catch (ErrorResponseException | InsufficientDataException | InternalException
                | InvalidKeyException | InvalidResponseException | IOException
                | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            log.error("Can't load image: {} from bucket: {}", path, bucket);
//            e.printStackTrace();
        }

        return null;
    }

    public void putObject(String bucket, String path, String objType, byte[] obj) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                log.error("Bucket of name '{}' does not exist!", bucket);
            }

            ByteArrayInputStream input = new ByteArrayInputStream(obj);

            ObjectWriteResponse res = minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucket).object(path).stream(input, obj.length, -1)
                            .contentType(objType)
                            .build());
            res.etag();

        } catch (ErrorResponseException | InsufficientDataException | InternalException
                | InvalidKeyException | InvalidResponseException | IOException
                | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            log.error("Can't write image: {} to bucket: {}", path, bucket);
//            e.printStackTrace();
        }

    }
}
