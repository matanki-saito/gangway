package cloud.popush.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import cloud.popush.envoy.AuthResult;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Setter;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ElasticSearchAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Setter
    private String key;

    @Setter
    private Integer port;

    @Setter
    private String host;

    @Setter
    private String username;

    @Setter
    private String password;

    private ElasticsearchClient elasticsearchClient;

    @Override
    protected void append(ILoggingEvent eventObject) {
        eventObject
                .getKeyValuePairs()
                .stream()
                .filter(x -> x.key.equals(key) && x.value instanceof List)
                .map(x -> x.value)
                .forEach(x -> innerAppend((List) x));
    }

    @Override
    public void start() {
        elasticsearchClient = setupClient(host, port, username, password);
        super.start();
    }

    protected void innerAppend(List<? extends AuthResult> results) {
        var contexts = results.stream()
                .map(AuthResult::getContext)
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var indexName = contexts.getOrDefault("host", "-").toString().toLowerCase();

        // 初回index作成
        try {
            if (!elasticsearchClient.indices().exists(c -> c.index(indexName)).value()) {
                elasticsearchClient.indices().create(c -> c.index(indexName));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            // ESはUTCでしか処理できない。kibanaで表示を変更する
            LocalDateTime timestamp;
            if (contexts.containsKey("timestamp") && contexts.get("timestamp") instanceof LocalDateTime) {
                timestamp = (LocalDateTime) contexts.get("timestamp");
                contexts.remove("timestamp");
            } else {
                timestamp = LocalDateTime.now(ZoneId.of("UTC"));
            }

            contexts.put("@timestamp", timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index(indexName)
                    .id(UUID.randomUUID().toString())
                    .document(contexts)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ElasticsearchClient setupClient(String host, Integer port, String userName, String password) {
        final var credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(userName, password));

        final var restClientBuilder = RestClient.builder(new HttpHost(host, port));
        restClientBuilder.setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClientBuilder.build(), new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }
}
