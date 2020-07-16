package cn.toesbieya.jxc.gateway.config;

import cn.toesbieya.jxc.gateway.filter.SecurityFilter;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RouteConfig implements ApplicationEventPublisherAware {
    @Value("${spring.cloud.nacos.config.server-addr}")
    private String NACOS_SERVER_ADDR;

    private GatewayConfig config;

    private RouteDefinitionWriter writer;

    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void addListener() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, NACOS_SERVER_ADDR);
        ConfigService configService = NacosFactory.createConfigService(properties);

        configService.addListener(config.getDataId(), config.getGroup(), new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void receiveConfigInfo(String configInfo) {
                Yaml yaml = new Yaml();
                Map<String, Object> config = yaml.load(configInfo);
                config = (Map<String, Object>) config.get("spring");
                config = (Map<String, Object>) config.get("cloud");
                config = (Map<String, Object>) config.get("gateway");

                SecurityFilter.updateWhitelist((List<String>) config.get("whitelist"));

                List<Map<String, Object>> routes = (List<Map<String, Object>>) config.get("routes");

                if (routes == null) return;

                List<RouteDefinition> list =
                        routes
                                .stream()
                                .map(map -> {
                                    RouteDefinition route = new RouteDefinition();

                                    route.setId((String) map.get("id"));

                                    route.setUri(URI.create((String) map.get("uri")));

                                    List<String> predicates = (List<String>) map.get("predicates");

                                    if (predicates != null) {
                                        route.setPredicates(
                                                predicates
                                                        .stream()
                                                        .map(PredicateDefinition::new)
                                                        .collect(Collectors.toList())
                                        );
                                    }

                                    List<String> filters = (List<String>) map.get("filters");

                                    if (filters != null) {
                                        route.setFilters(
                                                filters
                                                        .stream()
                                                        .map(FilterDefinition::new)
                                                        .collect(Collectors.toList())
                                        );
                                    }

                                    return route;
                                })
                                .collect(Collectors.toList());

                update(list);

                //SentinelFilter.registerRules(list);
            }
        });
    }

    private void update(List<RouteDefinition> routes) {
        if (routes == null) return;

        routes.forEach(route -> {
            this.writer.delete(Mono.just(route.getId()));
            writer.save(Mono.just(route)).subscribe();
        });

        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Autowired
    public void setWriter(RouteDefinitionWriter writer) {
        this.writer = writer;
    }

    @Autowired
    public void setGatewayProperties(GatewayConfig config) {
        this.config = config;
    }
}
