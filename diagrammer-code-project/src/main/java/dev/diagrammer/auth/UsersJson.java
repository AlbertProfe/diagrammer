package dev.diagrammer.auth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Parses the credentials file. The file is a JSON array of
 * {@code userId}, {@code username}, {@code password}, and {@code displayName}.
 * The password must use the {@code {noop}} prefix.
 */
@Component
public class UsersJson {

    static final String PASSWORD_PREFIX = "{noop}";

    private final JsonMapper jsonMapper;

    public UsersJson() {
        this(JsonMapper.builder().build());
    }

    UsersJson(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public List<StoredUser> parse(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Users file is empty");
        }
        byte[] payload = stripBom(bytes);
        JsonNode root;
        try {
            root = jsonMapper.readTree(payload);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Users file is not valid JSON", ex);
        }
        if (root == null || !root.isArray()) {
            throw new IllegalArgumentException("Users file must be a JSON array");
        }

        List<StoredUser> users = new ArrayList<>();
        Set<String> userIds = new HashSet<>();
        Set<String> usernames = new HashSet<>();
        int index = 0;
        for (JsonNode node : root) {
            StoredUser user = readUser(node, index);
            if (!userIds.add(user.userId())) {
                throw new IllegalArgumentException("Duplicate userId at index " + index);
            }
            if (!usernames.add(user.username())) {
                throw new IllegalArgumentException("Duplicate username at index " + index);
            }
            users.add(user);
            index++;
        }
        return List.copyOf(users);
    }

    private static StoredUser readUser(JsonNode node, int index) {
        if (node == null || !node.isObject()) {
            throw new IllegalArgumentException("User at index " + index + " must be an object");
        }
        String userId = requiredText(node, "userId", index);
        String username = requiredText(node, "username", index);
        String password = requiredText(node, "password", index);
        String displayName = requiredText(node, "displayName", index);
        if (!password.startsWith(PASSWORD_PREFIX)) {
            throw new IllegalArgumentException(
                    "Password at index " + index + " must start with " + PASSWORD_PREFIX);
        }
        if (userId.contains("/") || userId.contains("\\") || userId.contains("..")) {
            throw new IllegalArgumentException("userId at index " + index + " must be a single path segment");
        }
        return new StoredUser(userId, username, password, displayName);
    }

    private static String requiredText(JsonNode node, String field, int index) {
        JsonNode value = node.get(field);
        if (value == null || !value.isString() || value.asString().isBlank()) {
            throw new IllegalArgumentException("User at index " + index + " is missing " + field);
        }
        return value.asString();
    }

    private static byte[] stripBom(byte[] bytes) {
        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xEF
                && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF) {
            byte[] stripped = new byte[bytes.length - 3];
            System.arraycopy(bytes, 3, stripped, 0, stripped.length);
            return stripped;
        }
        return bytes;
    }
}
