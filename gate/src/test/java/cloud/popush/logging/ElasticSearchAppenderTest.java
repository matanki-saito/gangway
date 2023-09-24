package cloud.popush.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
class ElasticSearchAppenderTest {

    @Test
    void timestampTest() {
        String print = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Test
    void sendLog() {
        var service = new ElasticSearchAppender();
        service.setHost("localhost");
        service.setPort(9200);
        service.setUsername("elastic");
        service.setPassword("hogehoge");
        //service.innerAppend(Map.of("host", "eu4wiki-post-log", "POD", Map.of("test", Map.of("a", 3))));
    }
}