package cloud.popush.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VpnapiDto {
    private String ip;
    private Security security;
    private Location location;
    private Network network;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Security {
        private boolean vpn;
        private boolean proxy;
        private boolean tor;
        private boolean relay;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Location {
        private String city;
        private String region;
        private String country;
        private String continent;
        @JsonProperty("region_code")
        private String regionCode;
        @JsonProperty("country_code")
        private String countryCode;
        @JsonProperty("continent_code")
        private String continentCode;
        private String latitude;
        private String longitude;
        @JsonProperty("time_zone")
        private String timeZone;
        @JsonProperty("locale_code")
        private String localeCode;
        @JsonProperty("metro_code")
        private String metroCode;
        @JsonProperty("is_in_european_union")
        private boolean isInEuropeanUnion;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Network {
        private String network;
        @JsonProperty("autonomous_system_number")
        private String autonomousSystemNumber;
        @JsonProperty("autonomous_system_organization")
        private String autonomousSystemOrganization;
    }
}
