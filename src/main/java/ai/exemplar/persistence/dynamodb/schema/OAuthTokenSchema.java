package ai.exemplar.persistence.dynamodb.schema;

import ai.exemplar.persistence.model.OAuthToken;
import ai.exemplar.utils.dynamodb.converters.LocalDateTimeTypeConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@DynamoDBTable(tableName = "OAuthToken")
public class OAuthTokenSchema {

    private static final String PRIMARY_KEY_DELIMITER = "_";

    private String key;

    private String token;

    private String refreshToken;

    private LocalDateTime created;

    private LocalDateTime updated;

    private LocalDateTime expiration;

    private LocalDateTime lastFetched;

    private String internalId;

    public OAuthTokenSchema() {
    }

    public OAuthTokenSchema(String key, String token, String refreshToken, LocalDateTime created, LocalDateTime updated, LocalDateTime expiration, LocalDateTime lastFetched, String internalId) {
        this.key = key;
        this.token = token;
        this.refreshToken = refreshToken;
        this.created = created;
        this.updated = updated;
        this.expiration = expiration;
        this.lastFetched = lastFetched;
        this.internalId = internalId;
    }

    @DynamoDBHashKey(attributeName = "key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @DynamoDBAttribute(attributeName = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @DynamoDBAttribute(attributeName = "refreshToken")
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeTypeConverter.class)
    @DynamoDBAttribute(attributeName = "created")
    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeTypeConverter.class)
    @DynamoDBAttribute(attributeName = "updated")
    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeTypeConverter.class)
    @DynamoDBAttribute(attributeName = "expiration")
    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeTypeConverter.class)
    @DynamoDBAttribute(attributeName = "lastFetched")
    public LocalDateTime getLastFetched() {
        return lastFetched;
    }

    public void setLastFetched(LocalDateTime lastFetched) {
        this.lastFetched = lastFetched;
    }

    @DynamoDBAttribute(attributeName = "internalId")
    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public static OAuthTokenSchema primaryKey(String username, String provider) {
        return new OAuthTokenSchema(
                username + PRIMARY_KEY_DELIMITER + provider,
                null, null, null, null, null, null, null
        );
    }

    public static OAuthTokenSchema fromModel(OAuthToken token) {
        return new OAuthTokenSchema(
                token.getId() + PRIMARY_KEY_DELIMITER + token.getProvider(),
                token.getToken(),
                token.getRefreshToken(),
                token.getCreated(),
                token.getUpdated(),
                token.getExpiration(),
                token.getLastFetched(),
                token.getInternalId()
        );
    }

    public OAuthToken toModel() {
        List<String> key = Arrays.asList(this.getKey()
                .split(PRIMARY_KEY_DELIMITER));
        return new OAuthToken(
                key.subList(0, key.size() - 1).stream()
                        .collect(Collectors.joining(PRIMARY_KEY_DELIMITER)),
                key.get(key.size() - 1),
                this.getToken(),
                this.getRefreshToken(),
                this.getCreated(),
                this.getUpdated(),
                this.getExpiration(),
                this.getLastFetched(),
                this.getInternalId()
        );
    }
}
