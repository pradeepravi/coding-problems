package api.v1.dogBreed.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexmodelbuilding.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class S3UploadsService {
    private static final Logger LOG = LoggerFactory.getLogger(S3UploadsService.class);

    private final static String S3_BUCKET = "pradeep-dive";

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

    /**
     * Method stores file to S3 Bucket
     * @param fileName
     * @param file
     */
    void uploadFile(final String fileName, final File file) throws DogBreedException {
        try {
            this.s3client.putObject(S3_BUCKET, fileName, file);
            LOG.info("uploaded file" + file.getAbsolutePath() + "to S3 with KEY " + fileName);
        } catch (SdkClientException e) {
            LOG.error("Exception when trying to store file ["+file.getAbsolutePath()+"]");
            throw new DogBreedException("Unable to store image to S3");
        }
    }

//    String getFile(final String fileName) {
//        try {
//            S3Object putObjectResult = this.s3client.getObject(S3_BUCKET, fileName);
//            LOG.info("uploaded file location:"+putObjectResult.getRedirectLocation());
//            return putObjectResult.getRedirectLocation();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        throw new NotFoundException("File - "+fileName+" Not Found");
//    }

    /**
     * Method deletes File from S3 Storage for the Key (or fileName) passed
     * @param fileName
     * @return
     */
    boolean delete(final String fileName) throws DogBreedException {
        try {
            this.s3client.deleteObject(new DeleteObjectRequest(S3_BUCKET, fileName));
        } catch (SdkClientException e) {
            LOG.error("Exception when trying to delete file in s3 with key ["+fileName+"]");
            throw new DogBreedException("Unable to delete image in S3");
        }
        return true;
    }

    public String getEndpointUrl(){
        return endpointUrl;
    }
}
