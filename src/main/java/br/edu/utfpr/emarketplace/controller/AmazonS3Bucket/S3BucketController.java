package br.edu.utfpr.emarketplace.controller.AmazonS3Bucket;

import br.edu.utfpr.emarketplace.service.AmazonS3Bucket.AmazonS3BucketServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("file")
@RequiredArgsConstructor
public class S3BucketController {
    private final AmazonS3BucketServiceImpl amazonS3BucketServiceImpl;

//    @PostMapping("uploadFile")
//    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
//        return this.amazonS3BucketServiceImpl.uploadFile(file);
//    }

    @PostMapping("deleteFile")
    public String deleteFile(@RequestBody String fileURL) {
        return this.amazonS3BucketServiceImpl.deleteFileFromBucket(fileURL);
    }
}
