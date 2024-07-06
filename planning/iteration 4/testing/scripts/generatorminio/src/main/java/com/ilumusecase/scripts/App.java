package com.ilumusecase.scripts;

import java.util.Random;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.opencsv.CSVWriter;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;

public final class App {
    private App() {
    }

    public static void generateCsv(String filePath, int rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] header = { "number", "somedata"};
            writer.writeNext(header);

            Random random = new Random();
            for (int i = 0; i < rows; i++) {
                String[] data = {
                    String.valueOf(random.nextInt(9) + 1),
                    String.valueOf(random.nextLong())
                };
                writer.writeNext(data);
            }
        }
    }

    public static void uploadFile(String endpoint, String accessKey, String secretKey, String bucketName, String objectName, String filePath) {
        try {
            MinioClient minioClient =
                MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            try {
                minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                        new FileInputStream(new File(filePath)), new File(filePath).length(), -1)
                        .build());
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            System.out.println("File uploaded successfully");
        } catch (MinioException | IOException e) {
            System.err.println("Error occurred: " + e);
        }
    }

    public static void main(String[] args) {
        
        try {
            generateCsv("./data.csv", 1000);
            uploadFile("http://localhost:9000", "minioadmin", "minioadmin", "ilum-files", "jobs-manager/internal_668918b352348c74eaa837d1/data.csv", "data.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
