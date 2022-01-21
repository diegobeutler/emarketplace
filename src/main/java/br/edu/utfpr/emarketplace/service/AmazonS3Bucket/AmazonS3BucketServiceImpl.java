package br.edu.utfpr.emarketplace.service.AmazonS3Bucket;

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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class AmazonS3BucketServiceImpl {

    private AmazonS3 amazonS3;

    @Value("${endpointUrl}")
    private String endpointUrl;
    @Value("${bucketName}")
    private String bucketName;
    @Value("${pathUser}")
    private String pathUser;
    @Value("${accessKey}")
    private String accessKey;
    @Value("${secretKey}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
//        this.amazonS3 = new AmazonS3Client(credentials);
        this.amazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.SA_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }

//    public String uploadFile(MultipartFile multipartFile) {
//        String fileURL = "";
//        try {
//            File file = convertMultipartFileToFile(multipartFile);
//            String fileName = multipartFile.getOriginalFilename();
//            uploadFileToBucket(fileName, file);
//            fileURL = endpointUrl + "/" + bucketName + pathUser + "/" + fileName;
//            file.delete();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return fileURL;
//    }
    public String uploadFile(File file) {
        String fileURL = "";
        try {
            uploadFileToBucket(file.getName(), file);
            fileURL = endpointUrl + "/" + bucketName + pathUser + "/" + file.getName();
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileURL;
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    private void uploadFileToBucket(String fileName, File file) {
        amazonS3.putObject(new PutObjectRequest(bucketName + pathUser, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromBucket(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName + pathUser, fileName));
        return "Deletion Successful";
    }

}

