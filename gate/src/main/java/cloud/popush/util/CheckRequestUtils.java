package cloud.popush.util;

import cloud.popush.exception.ArgumentException;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.experimental.UtilityClass;

import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@UtilityClass
public class CheckRequestUtils {
    public static String getIpStr(CheckRequest checkRequest) throws ArgumentException {
        var map = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHeadersMap();

        if (!map.containsKey("x-forwarded-for")) {
            throw new ArgumentException("Not defined: x-forwarded-for");
        }

        return map.get("x-forwarded-for");
    }

    public static String getUserAgent(CheckRequest checkRequest) throws ArgumentException {
        var map = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHeadersMap();

        if (!map.containsKey("user-agent")) {
            throw new ArgumentException("Not defined: User-Agent");
        }

        return map.get("user-agent");
    }

    public static String getHost(CheckRequest checkRequest) {
        return checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHost();
    }

    public static String getPath(CheckRequest checkRequest) {
        var pathWithGetParams = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getPath();

        var array = pathWithGetParams.split("\\?");

        return array[0];
    }

    public static String getPathWithParam(CheckRequest checkRequest) {
        var pathWithGetParams = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getPath();

        return pathWithGetParams;
    }

    public static String getScheme(CheckRequest checkRequest) {
        return checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getScheme();
    }

    public static String getMethod(CheckRequest checkRequest) {
        return checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getMethod();
    }

    public static Map<String, String> getQuery(CheckRequest checkRequest) throws UnsupportedEncodingException {
        var request = checkRequest.getAttributes().getRequest().getHttp();

        if (request == null) {
            return new HashMap<>();
        }

        var method = request.getMethod();
        if (method == null || method.isEmpty() || !method.toLowerCase(Locale.ROOT).equals("post")) {
            return new HashMap<>();
        }

        var body = request.getBody();
        if (body == null || body.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> query_pairs = new LinkedHashMap<String, String>();

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8),
                    URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }

        return query_pairs;
    }

    public static Optional<List<HttpCookie>> getCookie(CheckRequest checkRequest) {
        var headersMap = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHeadersMap();

        if (!headersMap.containsKey("cookie")) {
            return Optional.empty();
        }

        var cookies = HttpCookie.parse(headersMap.get("cookie"));

        return Optional.of(cookies);
    }

    public static String getUrl(CheckRequest checkRequest) {
        return "%s://%s%s".formatted(getScheme(checkRequest), getHost(checkRequest), getPathWithParam(checkRequest));
    }
}
