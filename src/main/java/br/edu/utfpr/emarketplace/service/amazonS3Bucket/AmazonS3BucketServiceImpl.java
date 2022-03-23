package br.edu.utfpr.emarketplace.service.amazonS3Bucket;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class AmazonS3BucketServiceImpl {

    private AmazonS3 amazonS3;

    @Value("${endpointUrl}")
    private String endpointUrl;
    @Value("${bucketName}")
    private String bucketName;
    @Value("${pathUser}")
    private String pathUser;
    @Value("${pathAd}")
    private String pathAd;
    @Value("${accessKey}")
    private String accessKey;
    @Value("${secretKey}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.amazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.SA_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }

    public String uploadFile(File file, boolean isImageProfile) {
        String fileURL = "";
        String path = bucketName + (isImageProfile ? pathUser : pathAd);
        try {
            uploadFileToBucket(path, file.getName(), file);
            fileURL = endpointUrl + "/" + path + "/" + file.getName();
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileURL;
    }

    private void uploadFileToBucket(String path, String fileName, File file) {
        amazonS3.putObject(new PutObjectRequest(path, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public void deleteFileFromBucket(String fileName, boolean isImageProfile) {
        String path = bucketName + (isImageProfile ? pathUser : pathAd);
        amazonS3.deleteObject(new DeleteObjectRequest(path, fileName));
    }
}

