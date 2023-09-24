package cloud.popush.envoy;

import cloud.popush.exception.MachineException;
import cloud.popush.util.CheckRequestUtils;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.envoyproxy.envoy.service.auth.v3.DeniedHttpResponse;
import io.envoyproxy.envoy.type.v3.HttpStatus;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@GRpcService
public class EnvoyExternalAuthService extends AuthorizationGrpc.AuthorizationImplBase {

    private final List<GateFilter> gateFilterList;
    private final MappingProperties mappingProperties;

    private AuthResult innerCheck(GateFilter filter, CheckRequest request) {
        AuthResult result;
        try {
            result = filter.check(request);
        } catch (MachineException e) {
            result = new AuthResultNg("Exception");
        }

        result.filterName = filter.getClass().getName();

        return result;
    }

    protected List<String> getActiveFilterNames(String host, String path, String method) {
        var map = mappingProperties.getFilter();
        if (!map.containsKey(host)) {
            return List.of();
        }

        if (!List.of("POST", "GET").contains(method.toUpperCase())) {
            return List.of();
        }

        Map<String, Map<String, Boolean>> pathMap = Objects.requireNonNullElse("GET".equalsIgnoreCase(method)
                ? map.get(host).getGetMethods()
                : map.get(host).getPostMethods(), Map.of());

        return pathMap.entrySet()
                .stream()
                .filter(x -> path.matches(x.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(Map::of)
                .entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey).toList();
    }

    private CheckResponse buildSuccessResponse() {
        return CheckResponse.newBuilder()
                .setStatus(Status
                        .newBuilder()
                        .setCode(Code.OK_VALUE)
                        .setMessage("OK")
                        .build())
                .build();
    }

    private CheckResponse buildFailResponse(
            List<AuthResultNg> authResultNgs
    ) {
        var responseMessageAll = String.join("\n", authResultNgs
                .stream()
                .map(AuthResultNg::getResponseMessage)
                .filter(x -> !x.isBlank()).toList());

        var responseHeadersAll = authResultNgs.stream()
                .map(AuthResultNg::getResponseHeader)
                .flatMap(m -> m.entrySet().stream())
                .map(x -> HeaderValueOption.newBuilder().setHeader(HeaderValue.newBuilder()
                        .setKey(x.getKey())
                        .setValue(x.getValue())
                        .build()).build())
                .toList();

        var statusCode = authResultNgs.stream()
                .map(AuthResultNg::getResponseStatusCode)
                .findFirst()
                .orElseGet(() -> 403);

        return CheckResponse.newBuilder()
                .setDeniedResponse(DeniedHttpResponse.newBuilder()
                        .setBody(responseMessageAll)
                        .setStatus(HttpStatus.newBuilder().setCodeValue(statusCode))
                        .addAllHeaders(responseHeadersAll)
                        .build())
                .setStatus(Status
                        .newBuilder()
                        .setCode(Code.PERMISSION_DENIED_VALUE)
                        .setMessage("NG")
                        .build())
                .build();
    }

    @Override
    public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
        var host = CheckRequestUtils.getHost(request);
        var path = CheckRequestUtils.getPath(request);
        var method = CheckRequestUtils.getMethod(request);

        var activeFilterNames = getActiveFilterNames(host, path, method);

        var authResults = new ArrayList<>(gateFilterList
                .stream()
                .filter(filter -> activeFilterNames.contains(filter.getId()))
                .map(filter -> innerCheck(filter, request))
                .toList());

        var isPass = authResults.stream().allMatch(AuthResult::isOk);

        authResults.add(new AuthReasonOk(Map.of("pass", isPass)));

        var fails = authResults
                .stream()
                .filter(x -> x instanceof AuthResultNg)
                .map(x -> (AuthResultNg) x)
                .toList();

        var response = isPass ? buildSuccessResponse() : buildFailResponse(fails);

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        log.atInfo().addKeyValue("POS", authResults).log();
    }
}
