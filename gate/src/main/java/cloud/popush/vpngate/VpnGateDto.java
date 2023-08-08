package cloud.popush.vpngate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "HostName",
        "IP",
        "Score",
        "Ping",
        "Speed",
        "CountryLong",
        "CountryShort",
        "NumVpnSessions",
        "Uptime",
        "TotalUsers",
        "TotalTraffic",
        "LogType",
        "Operator",
        "Message",
        "OpenVPN_ConfigData_Base64"
})
public class VpnGateDto {
    @JsonProperty("HostName")
    private String hostName;
    @JsonProperty("IP")
    private String ip;
    @JsonProperty("Score")
    private Integer score;
    @JsonProperty("Ping")
    private String ping;
    @JsonProperty("Speed")
    private BigDecimal speed;
    @JsonProperty("CountryLong")
    private String countryLong;
    @JsonProperty("CountryShort")
    private String countryShort;
    @JsonProperty("NumVpnSessions")
    private Integer numVpnSessions;
    @JsonProperty("Uptime")
    private BigDecimal uptime;
    @JsonProperty("TotalUsers")
    private Integer totalUsers;
    @JsonProperty("TotalTraffic")
    private BigDecimal totalTraffic;
    @JsonProperty("LogType")
    private String logType;
    @JsonProperty("Operator")
    private String operator;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("OpenVPN_ConfigData_Base64")
    private String openVPNConfigDataBase64;
}
