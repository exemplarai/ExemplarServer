package ai.exemplar.api.spotify;

import ai.exemplar.api.spotify.model.AudioFeaturesObject;
import ai.exemplar.api.spotify.model.PlayHistoryObject;

import java.time.LocalDateTime;
import java.util.List;

public interface SpotifyApiProvider {

    List<PlayHistoryObject> getRecentlyPlayed(String bearer, LocalDateTime after);

    List<AudioFeaturesObject> getAudioFeatures(String bearer, List<String> ids);
}
