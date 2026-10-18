package dev.diagrammer.auth;

import java.io.IOException;
import java.util.List;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Reads {@code auth/users.json} from one S3 bucket. Bucket, region, and AWS
 * credentials are supplied by the caller from the environment. This class
 * never embeds them.
 */
public class S3UserStore implements UserStore {

    public static final String USERS_KEY = "auth/users.json";

    private final S3Client s3;
    private final String bucket;
    private final String usersKey;
    private final UsersJson usersJson;

    public S3UserStore(S3Client s3, String bucket, String usersKey, UsersJson usersJson) {
        if (s3 == null) {
            throw new IllegalArgumentException("s3 client is required");
        }
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException("S3 bucket is required");
        }
        if (usersKey == null || usersKey.isBlank()) {
            throw new IllegalArgumentException("users object key is required");
        }
        this.s3 = s3;
        this.bucket = bucket;
        this.usersKey = usersKey;
        this.usersJson = usersJson;
    }

    @Override
    public List<StoredUser> findAll() {
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(usersKey).build();
        try (ResponseInputStream<GetObjectResponse> response = s3.getObject(request)) {
            return usersJson.parse(response.readAllBytes());
        } catch (IOException | S3Exception ex) {
            throw new IllegalStateException("Could not read " + usersKey + " from bucket " + bucket, ex);
        }
    }
}
