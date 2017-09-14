package ai.exemplar.api.spotify.impl;

import ai.exemplar.api.spotify.SpotifyApiProvider;
import ai.exemplar.api.spotify.model.AudioFeaturesObject;
import ai.exemplar.api.spotify.model.PlayHistoryObject;
import ai.exemplar.utils.json.GsonFabric;
import com.amazonaws.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public class SpotifyApiProviderImpl implements SpotifyApiProvider {

    static final Logger log = Logger.getLogger(SpotifyApiProviderImpl.class);

    private final Gson gson = GsonFabric.gson();

    @Override
    public List<PlayHistoryObject> getRecentlyPlayed(String bearer, LocalDateTime after) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.spotify.com/v1/me/player/recently-played");

            request.setURI(
                    new URIBuilder(request.getURI())
                            .addParameter("after", Long.toString(after
                                    .toInstant(ZoneOffset.UTC).toEpochMilli()))
                            .build()
            );

            request.addHeader("Authorization", String.format("Bearer %s", bearer));

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("recently-played request failed: " +
                        response.getStatusLine().getStatusCode() + " " +
                        response.getStatusLine().getReasonPhrase());
            }

            String responseEntity = IOUtils.toString(response
                    .getEntity().getContent());

            RecentlyPlayedResponse recentlyPlayedResponseBody = gson.fromJson(
                    responseEntity,
                    RecentlyPlayedResponse.class
            );

            return recentlyPlayedResponseBody.getItems();

        } catch (Throwable e) {
            log.error("getRecentlyPlayed request failed:", e);

            throw new RuntimeException(e);
        }
    }

    public static class RecentlyPlayedResponse {

        private List<PlayHistoryObject> items;

        public List<PlayHistoryObject> getItems() {
            return items;
        }

        public void setItems(List<PlayHistoryObject> items) {
            this.items = items;
        }
    }

    @Override
    public List<AudioFeaturesObject> getAudioFeatures(String bearer, List<String> ids) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.spotify.com/v1/audio-features/");

            request.setURI(
                    new URIBuilder(request.getURI())
                            .addParameter("ids", ids.stream().collect(Collectors
                                    .joining(",")))
                            .build()
            );

            request.addHeader("Authorization", String.format("Bearer %s", bearer));

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("audio-features request failed: " +
                        response.getStatusLine().getStatusCode() + " " +
                        response.getStatusLine().getReasonPhrase());
            }

            String responseEntity = IOUtils.toString(response
                    .getEntity().getContent());

            SeveralAudioFeaturesResponse audioFeaturesResponse = gson.fromJson(
                    responseEntity,
                    SeveralAudioFeaturesResponse.class
            );

            return audioFeaturesResponse.getItems();

        } catch (Throwable e) {
            log.error("getAudioFeatures request failed:", e);

            throw new RuntimeException(e);
        }
    }

    public static class SeveralAudioFeaturesResponse {

        @SerializedName("audio_features")
        private List<AudioFeaturesObject> items;

        public List<AudioFeaturesObject> getItems() {
            return items;
        }

        public void setItems(List<AudioFeaturesObject> items) {
            this.items = items;
        }
    }
}
