package cloud.popush.vpngate;

import cloud.popush.cache.CacheExpiring;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VpnGateService {

    @Cacheable("getVpnGateServiceList")
    @CacheExpiring(600)
    public List<VpnGateDto> getList() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate
                .exchange("http://www.vpngate.net/api/iphone/",
                        HttpMethod.GET,
                        null,
                        String.class);

        if (!StringUtils.hasText(response.getBody())) {
            throw new RuntimeException();
        }

        // 1行目（*vpn_servers）は破棄
        // 2行目の先頭の#を破棄
        var list = response.getBody().split("\r\n");
        StringBuilder data = new StringBuilder();
        for (var i = 0; i < list.length; i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {
                data.append(list[i].substring(1));
                data.append("\r\n");
            } else {
                data.append(list[i]);
                data.append("\r\n");
            }
        }

        CsvMapper csvMapper = new CsvMapper();

        CsvSchema csvSchema = csvMapper
                .schemaFor(VpnGateDto.class)
                .withHeader();

        List<VpnGateDto> rtn = new ArrayList<>();
        try (MappingIterator<VpnGateDto> objectMappingIterator = csvMapper.readerFor(VpnGateDto.class)
                .with(csvSchema)
                .readValues(data.toString())) {

            while (objectMappingIterator.hasNext()) {
                rtn.add(objectMappingIterator.next());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rtn;
    }
}
