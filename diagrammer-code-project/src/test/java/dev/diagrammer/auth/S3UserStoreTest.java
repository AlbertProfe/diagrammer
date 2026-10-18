package dev.diagrammer.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

class S3UserStoreTest {

    private static final String JSON = """
            [
              {
                "userId": "u1",
                "username": "albert",
                "password": "{noop}secret",
                "displayName": "Albert"
              },
              {
                "userId": "u2",
                "username": "ada",
                "password": "{noop}lovelace",
                "displayName": "Ada Lovelace"
              }
            ]
            """;

    @Test
    void readsAuthUsersJsonFromTheBucketAndParsesIt() {
        S3Client s3 = org.mockito.Mockito.mock(S3Client.class);
        when(s3.getObject(any(GetObjectRequest.class))).thenAnswer(invocation -> objectWith(JSON));
        S3UserStore store = new S3UserStore(s3, "diagrammer-bucket", S3UserStore.USERS_KEY, new UsersJson());

        assertThat(store.findAll())
                .containsExactly(
                        new StoredUser("u1", "albert", "{noop}secret", "Albert"),
                        new StoredUser("u2", "ada", "{noop}lovelace", "Ada Lovelace"));
        assertThat(store.findByUsername("albert"))
                .get()
                .isEqualTo(new StoredUser("u1", "albert", "{noop}secret", "Albert"));

        ArgumentCaptor<GetObjectRequest> request = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3, atLeastOnce()).getObject(request.capture());
        assertThat(request.getValue().bucket()).isEqualTo("diagrammer-bucket");
        assertThat(request.getValue().key()).isEqualTo("auth/users.json");
    }

    @Test
    void rejectsAFileThatIsNotTheExpectedArray() {
        S3Client s3 = org.mockito.Mockito.mock(S3Client.class);
        when(s3.getObject(any(GetObjectRequest.class))).thenReturn(objectWith("{\"userId\":\"u1\"}"));
        S3UserStore store = new S3UserStore(s3, "diagrammer-bucket", S3UserStore.USERS_KEY, new UsersJson());

        assertThatThrownBy(store::findAll).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAPasswordWithoutTheNoopPrefix() {
        S3Client s3 = org.mockito.Mockito.mock(S3Client.class);
        when(s3.getObject(any(GetObjectRequest.class)))
                .thenReturn(objectWith(
                        """
                        [{"userId":"u1","username":"albert","password":"secret","displayName":"Albert"}]
                        """));
        S3UserStore store = new S3UserStore(s3, "diagrammer-bucket", S3UserStore.USERS_KEY, new UsersJson());

        assertThatThrownBy(store::findAll)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("{noop}");
    }

    @Test
    void rejectsAUserMissingDisplayName() {
        S3Client s3 = org.mockito.Mockito.mock(S3Client.class);
        when(s3.getObject(any(GetObjectRequest.class)))
                .thenReturn(objectWith(
                        """
                        [{"userId":"u1","username":"albert","password":"{noop}secret"}]
                        """));
        S3UserStore store = new S3UserStore(s3, "diagrammer-bucket", S3UserStore.USERS_KEY, new UsersJson());

        assertThatThrownBy(store::findAll)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("displayName");
    }

    private static ResponseInputStream<GetObjectResponse> objectWith(String json) {
        return new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                AbortableInputStream.create(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
    }
}
