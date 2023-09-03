package cloud.popush.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private final ElasticsearchClient elasticsearchClient;

    public void sendLog(String indexName, Map<String, Object> log) {
        aIndex(indexName);

        try {
            log.put("@timestamp", LocalDateTime.now(ZoneId.of("Asia/Tokyo"))
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index(indexName)
                    .id(UUID.randomUUID().toString())
                    .document(log)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean aIndex(String name) {
        try {
            if (!elasticsearchClient.indices().exists(c -> c.index(name))
                    .value()) {
                elasticsearchClient.indices().create(c -> c.index(name));
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}