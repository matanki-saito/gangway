package cloud.popush.util;

import cloud.popush.exception.ArgumentException;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NetUtils {
    public static String getIpStr(CheckRequest checkRequest) throws ArgumentException {
        var map = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHeadersMap();

        if (map.containsKey("x-forwarded-for")) {
            throw new ArgumentException("Not defined: x-forwarded-for");
        }

        return map.get("x-forwarded-for");
    }
}
