package cloud.popush.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class ElasticSearchAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Setter
    private Integer port;

    @Setter
    private String host;

    @Setter
    private String username;

    @Setter
    private String password;

    private ElasticsearchClient elasticsearchClient;

    private void setUpIndex(String indexName) {
        // 初回index作成
        try {
            if (!elasticsearchClient.indices().exists(c -> c.index(indexName)).value()) {
                elasticsearchClient.indices().create(c -> c.index(indexName));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fixTimestamp(Map<String, Object> contexts) {
        // ESはUTCでしか処理できない。kibanaで表示を変更する
        LocalDateTime timestamp;
        if (contexts.containsKey("timestamp") && contexts.get("timestamp") instanceof LocalDateTime) {
            timestamp = (LocalDateTime) contexts.get("timestamp");
            contexts.remove("timestamp");
        } else {
            timestamp = LocalDateTime.now(ZoneId.of("UTC"));
        }
        contexts.put("@timestamp", timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private List<AuthResult> getAuths(ILoggingEvent eventObject) {
        if (eventObject.getArgumentArray().length == 0) {
            return List.of();
        }

        var first = eventObject.getArgumentArray()[0];
        if (!(first instanceof List<?> aw)) {
            return List.of();
        }

        return aw.stream()
                .filter(x -> x instanceof AuthResult)
                .map(x -> (AuthResult) x)
                .toList();
    }

    private Map<String, Object> getContexts(List<AuthResult> list) {
        return list.stream()
                .map(AuthResult::getContext)
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(address1, address2) -> {
                    return address1;
                }));
    }

    private Map<String, Object> aggFailReasons(List<AuthResult> list) {
        return list.stream()
                .filter(x -> x instanceof AuthResultNg)
                .collect(Collectors.toMap(
                        x -> "fail.%s".formatted(x.getFilterName()),
                        AuthResult::getReason));
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            var auths = getAuths(eventObject);
            var contexts = getContexts(auths);
            var indexName = contexts.getOrDefault("target.host", "-").toString().toLowerCase();
            var id = UUID.randomUUID().toString();

            setUpIndex(indexName);
            fixTimestamp(contexts);
            contexts.putAll(aggFailReasons(auths));


            elasticsearchClient.index(i -> i
                    .index(indexName)
                    .id(id)
                    .document(contexts));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void start() {
        try {
            elasticsearchClient = setupClient(host, port, username, password);
            super.start();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private ElasticsearchClient setupClient(String host, Integer port, String userName, String password) {
        final var credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(userName, password));

        final var restClientBuilder = RestClient.builder(new HttpHost(host, port, "http"));
        restClientBuilder.setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClientBuilder.build(), new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }
}
