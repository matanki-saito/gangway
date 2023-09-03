package cloud.popush.proxy;

import cloud.popush.exception.OtherSystemException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class VpnapiService {

    @Value("${proxy.vpnapi.key}")
    private String apiKey;

    //@Cacheable("VpnapiServicegetInfo")
    //@CacheExpiring(800)
    public VpnapiDto getInfo(String ipStr) throws OtherSystemException {
        RestTemplate restTemplate = new RestTemplate();
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(
                        "https://vpnapi.io/api/%s".formatted(ipStr))
                .queryParam("key", apiKey)
                .encode()
                .toUriString();

        ResponseEntity<VpnapiDto> response = restTemplate
                .exchange(urlTemplate,
                        HttpMethod.GET,
                        null,
                        VpnapiDto.class);

        if (!response.getStatusCode().is2xxSuccessful() || Objects.isNull(response.getBody())) {
            throw new OtherSystemException("VPNAPI API Error");
        }

        return response.getBody();
    }
}
