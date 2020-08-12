package cn.toesbieya.jxc.gateway.filter;

import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.gateway.config.GatewayConfig;
import cn.toesbieya.jxc.gateway.config.ResourceConfig;
import com.alibaba.fastjson.JSON;
import cn.toesbieya.jxc.common.constant.SessionConstant;
import cn.toesbieya.jxc.common.util.SessionUtil;
import cn.toesbieya.jxc.common.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.server.PathContainer.parsePath;

@Component
@Slf4j
public class SecurityFilter {
    private GatewayConfig gatewayConfig;

    private static final PathPatternParser pathPatternParser = new PathPatternParser();

    private static List<PathPattern> whitelistPatterns = new ArrayList<>();

    @Bean
    @Order(-1)
    public GlobalFilter globalFilter() {
        updateWhitelist(gatewayConfig.getWhitelist());

        return ((exchange, chain) -> {
            String requestUrl = exchange.getRequest().getURI().getPath();

            PathContainer path = parsePath(requestUrl);

            //放行白名单内的url
            if (Util.some(whitelistPatterns, pattern -> pattern.matches(path))) {
                return chain.filter(exchange);
            }

            ServerHttpResponse response = exchange.getResponse();

            String token = exchange.getRequest().getHeaders().getFirst(SessionConstant.TOKEN_KEY);

            UserVo user = SessionUtil.get(token);

            //未登录
            if (user == null) {
                return responseJSON(response, R.requireLogin());
            }

            //没有权限
            if (!ResourceConfig.authority(user, requestUrl)) {
                return responseJSON(response, R.noPermission());
            }

            return chain.filter(exchange);
        });
    }

    private Mono<Void> responseJSON(ServerHttpResponse response, R result) {
        byte[] bytes = JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8);

        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        response.setStatusCode(HttpStatus.OK);

        response.getHeaders().add("Content-Type", "application/json; charset=utf-8");

        return response.writeWith(Mono.just(buffer));
    }

    public static void updateWhitelist(Collection<String> list) {
        if (list == null) {
            whitelistPatterns = new ArrayList<>();
            return;
        }

        whitelistPatterns =
                list
                        .stream()
                        .map(pathPatternParser::parse)
                        .collect(Collectors.toList());
    }

    @Autowired
    public void setGatewayProperties(GatewayConfig gatewayConfig) {
        this.gatewayConfig = gatewayConfig;
    }
}
