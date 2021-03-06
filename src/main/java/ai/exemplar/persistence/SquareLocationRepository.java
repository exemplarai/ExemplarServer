package ai.exemplar.persistence;

import ai.exemplar.persistence.dynamodb.schema.square.LocationSchema;

import java.util.List;

public interface SquareLocationRepository {

    List<LocationSchema> list(String account);

    LocationSchema get(String account, String id);

    void save(LocationSchema location);

    void batchSave(List<LocationSchema> batch);
}
