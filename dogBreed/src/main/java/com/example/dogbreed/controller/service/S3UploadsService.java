package com.example.dogbreed.controller.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class S3UploadsService {
    private static final Logger LOG = LoggerFactory.getLogger(S3UploadsService.class);

    final static  AWSCredentials credentials = new BasicAWSCredentials("AKIAQEKYLT2L4A4OFV6U", "MMJEB57Sk1Gxpbo9alusK7uoV/eugMcblsjHU03Q");
    final static String S3_BUCKET = "pradeep-dive";

    private AmazonS3 s3client;

    @Value("${aws.endpointUrl}")
    private String endpointUrl;
    @Value("${aws.bucketName}")
    private String bucketName;
    @Value("${aws.accessKey}")
    private String accessKey;
    @Value("${aws.secretKey}")
    private String secretKey;
    @Value("${aws.region}")
    private String region;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.fromName(region))
                .build();
    }

    void uploadFile(final String fileName, final File file) {
        try {
            PutObjectResult putObjectResult = this.s3client.putObject(S3_BUCKET, fileName, file);
            LOG.info("uploaded file"+file.getAbsolutePath()+" KEY "+fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
