package cloud.popush.util;

import cloud.popush.exception.ArgumentException;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.experimental.UtilityClass;
import org.apache.http.entity.ContentType;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;

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
        return checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getPath();
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

    public static Optional<String> getContentType(CheckRequest checkRequest) {
        var headersMap = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHeadersMap()
                .entrySet()
                .stream()
                .filter(x -> "Content-Type".toLowerCase().equals(x.getKey()))
                .findAny();

        return headersMap.map(Map.Entry::getValue);
    }

    private static final Pattern pattern = Pattern.compile("content-disposition: form-data; name=\"(.*)\"", Pattern.CASE_INSENSITIVE);

    public static Map<String, Object> getQuery(CheckRequest checkRequest) throws UnsupportedEncodingException {
        var request = checkRequest.getAttributes().getRequest().getHttp();

        var method = request.getMethod();
        if (method.isEmpty() || !method.toLowerCase(Locale.ROOT).equals("post")) {
            return new HashMap<>();
        }

        var body = request.getRawBody();
        if (body.isEmpty()) {
            return new HashMap<>();
        }

        var contentType = getContentType(checkRequest);
        if (contentType.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> query_pairs = new LinkedHashMap<>();

        if (contentType.get().equals("application/x-www-form-urlencoded")) {
            String[] pairs = body.toString(UTF_8).split("&");

            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                var a = pair.substring(0, idx);
                var b = pair.substring(idx + 1);
                query_pairs.put(decode(a, UTF_8), decode(b, UTF_8));
            }
        } else if (contentType.get().startsWith("multipart/form-data")) {
            MultipartStream multipartStream = new MultipartStream(
                    new ByteArrayInputStream(body.toByteArray()),
                    ContentType.parse(contentType.get()).getParameter("boundary").getBytes(),
                    1024,
                    null);

            try {
                boolean nextPart = multipartStream.skipPreamble();
                while (nextPart) {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    String partHeaders = multipartStream.readHeaders();
                    multipartStream.readBodyData(output);

                    var m = pattern.matcher(partHeaders);
                    if (m.find()) {
                        query_pairs.put(m.group(1), output);
                    }

                    nextPart = multipartStream.readBoundary();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return query_pairs;
    }

    public static Map<String, String> getCookies(CheckRequest checkRequest) {
        var headersMap = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHeadersMap();

        if (!headersMap.containsKey("cookie")) {
            return Map.of();
        }

        return Arrays.stream(headersMap.get("cookie").split(";"))
                .map(x -> x.split("="))
                .collect(Collectors.toMap(x -> StringUtils.trimAllWhitespace(x[0]), x -> x.length > 1 ? x[1] : "-"));
    }

    public static String getUrl(CheckRequest checkRequest) {
        return "%s://%s%s".formatted(getScheme(checkRequest), getHost(checkRequest), getPathWithParam(checkRequest));
    }
}
