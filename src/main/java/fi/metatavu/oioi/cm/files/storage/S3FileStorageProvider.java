package fi.metatavu.oioi.cm.files.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import fi.metatavu.oioi.cm.files.FileMeta;
import fi.metatavu.oioi.cm.files.InputFile;
import fi.metatavu.oioi.cm.files.OutputFile;

/**
 * File storage provider for storing files in S3. 
 * 
 * @author Antti Leppä
 */
public class S3FileStorageProvider implements FileStorageProvider {
  
  private String region;
  private String bucket;
  private String prefix;
  
  @Override
  public void init() throws FileStorageException {
    region = System.getenv("S3_FILE_STORAGE_REGION");
    bucket = System.getenv("S3_FILE_STORAGE_BUCKET");
    prefix = System.getenv("S3_FILE_STORAGE_PREFIX");
    
    if (StringUtils.isBlank(region)) {
      throw new FileStorageException("S3_FILE_STORAGE_REGION is not set"); 
    }
    
    if (StringUtils.isBlank(bucket)) {
      throw new FileStorageException("S3_FILE_STORAGE_BUCKET is not set"); 
    }
    
    if (StringUtils.isBlank(prefix)) {
      throw new FileStorageException("S3_FILE_STORAGE_PREFIX is not set"); 
    }
    
    AmazonS3 client = getClient();
    
    if (!client.doesBucketExistV2(bucket)) {
      throw new FileStorageException(String.format("bucket '%s' does not exist", bucket)); 
    }
  }

  @Override
  public OutputFile store(InputFile inputFile) throws FileStorageException {
    AmazonS3 client = getClient();
    String key = UUID.randomUUID().toString();
    FileMeta meta = inputFile.getMeta();
    String folder = inputFile.getFolder();
    
    ObjectMetadata objectMeta = new ObjectMetadata();
    objectMeta.setContentType(meta.getContentType());
    objectMeta.addUserMetadata("x-file-name", meta.getFileName());
    
    try {
      Path tempFile = Files.createTempFile("upload", "s3");
      try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile())) {
        IOUtils.copy(inputFile.getData(), fileOutputStream);
      }

      try (FileInputStream fileInputStream = new FileInputStream(tempFile.toFile())) {
        client.putObject(new PutObjectRequest(bucket, String.format("%s/%s", folder, key), fileInputStream, objectMeta).withCannedAcl(CannedAccessControlList.PublicRead));
        return new OutputFile(meta, URI.create(String.format("%s/%s/%s", prefix, folder, key)));
      } catch (SdkClientException e) {
        throw new FileStorageException(e);
      }
    } catch (IOException e) {
      throw new FileStorageException(e);
    }
    
  }
  
  @Override
  public String getId() {
    return "S3";
  }
  
  /**
   * Returns initialized S3 client
   * 
   * @return initialized S3 client
   */
  private AmazonS3 getClient() {
    return AmazonS3ClientBuilder.standard().withRegion(region).build();  
  }

}
