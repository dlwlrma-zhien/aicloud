package com.lcyy.aicloud.util;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author: dlwlrma
 * @data 2024年10月28日 21:08
 * @Description: TODO:MinIoUtil工具类
 */
@Component
public class MinIoUtil {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    /**
     * 文件的上传
     * @author dlwlrma
     * @date 2024/10/28 21:22
     * @param fileName
     * @param inputStream
     * @param contentType
     * @return java.lang.String
     */
    public String upload(String fileName, InputStream inputStream, String contentType)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //先判断是否存在桶，存在就进行已存在的操作，不存在就创建一个桶。
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                         // 上传的文件大小，从-1到10485760，保证文件的完整性
                        .stream(inputStream, -1, 10485760)
                        .contentType(contentType)
                .build());
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .method(Method.GET)
                .build());
    }
}
