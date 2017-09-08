package ai.exemplar.persistence.dynamodb;

import ai.exemplar.persistence.SpotifyHistoryRepository;
import ai.exemplar.persistence.dynamodb.schema.spotify.PlayHistoryItemSchema;
import ai.exemplar.utils.dynamodb.CreateTableHelper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.ConversionSchemas;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTableMapper;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

public class DynamoDBSpotifyHistoryRepository implements SpotifyHistoryRepository {

    static final Logger log = Logger.getLogger(DynamoDBSpotifyHistoryRepository.class);

    private final DynamoDBTableMapper<PlayHistoryItemSchema, String, String> spotifyHistory;

    @Inject
    public DynamoDBSpotifyHistoryRepository(AmazonDynamoDB amazonDynamoDB) {
        final DynamoDBMapper mapper = new DynamoDBMapper(
                amazonDynamoDB,
                new DynamoDBMapperConfig.Builder()
                        .withConversionSchema(ConversionSchemas.V2)
                        .withTableNameOverride(new DynamoDBMapperConfig
                                .TableNameOverride("SpotifyPlayHistory"))
                        .build()
        );

        CreateTableHelper.createTableIfNotExists(
                amazonDynamoDB,
                mapper,
                PlayHistoryItemSchema.class,
                new ProvisionedThroughput(5L, 5L)
        );

        this.spotifyHistory = mapper.newTableMapper(PlayHistoryItemSchema.class);
    }

    @Override
    public void batchSave(List<PlayHistoryItemSchema> batch) {
        List<DynamoDBMapper.FailedBatch> failed = spotifyHistory
                .batchSave(batch);

        if (!failed.isEmpty()) {
            failed.stream()
                    .map(DynamoDBMapper.FailedBatch::getException)
                    .forEach(e -> log.error("spotify history item save exception: ", e));

            throw new RuntimeException("batch save left unprocessed items");
        }
    }
}