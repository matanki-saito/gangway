package cloud.popush.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.assertj.core.api.Assertions;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
class ElasticsearchServiceTest {

    @Test
    void timestampTest(){
        String print = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    }

    @Test
    void sendLog() {
        var service = new ElasticsearchService(buildTestClient());
        service.sendLog("eu4wiki-post-log", Map.of("test", Map.of("a", 3)));
    }

    @Test
    void createIndex() {
        var service = new ElasticsearchService(buildTestClient());
        var isSuccess = service.aIndex("test");
        Assertions.assertThat(isSuccess).isTrue();
    }

    private ElasticsearchClient buildTestClient() {
        final var credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "hogehoge"));

        final var restClientBuilder = RestClient.builder(new HttpHost("localhost", 9200));
        restClientBuilder.setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));


        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClientBuilder.build(), new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }
}