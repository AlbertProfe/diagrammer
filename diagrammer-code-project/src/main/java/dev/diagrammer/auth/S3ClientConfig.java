package dev.diagrammer.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * S3 client for the {@code s3} profile.
 *
 * <p>The bucket name is {@code DIAGRAMMER_S3_BUCKET}. The region is {@code
 * AWS_REGION}. Credentials are not read here: the AWS SDK for Java v2 default
 * credential provider chain resolves {@code AWS_ACCESS_KEY_ID} and {@code
 * AWS_SECRET_ACCESS_KEY}. There are no application properties for keys.
 */
@Configuration
@Profile("s3")
public class S3ClientConfig {

    static final String BUCKET_ENV = "DIAGRAMMER_S3_BUCKET";
    static final String REGION_ENV = "AWS_REGION";

    @Bean
    S3Client diagrammerS3Client() {
        String region = System.getenv(REGION_ENV);
        if (region == null || region.isBlank()) {
            throw new IllegalStateException(REGION_ENV + " is required for the s3 profile");
        }
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    UserStore s3UserStore(S3Client diagrammerS3Client, UsersJson usersJson) {
        String bucket = System.getenv(BUCKET_ENV);
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException(BUCKET_ENV + " is required for the s3 profile");
        }
        return new S3UserStore(diagrammerS3Client, bucket, S3UserStore.USERS_KEY, usersJson);
    }
}
