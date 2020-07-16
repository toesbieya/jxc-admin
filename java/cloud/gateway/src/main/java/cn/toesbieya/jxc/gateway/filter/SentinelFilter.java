package cn.toesbieya.jxc.gateway.filter;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SentinelFilter {
    /*private GatewayProperties gatewayProperties;

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public SentinelFilter(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                          ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void init() {
        registerRules(gatewayProperties.getRoutes());

        GatewayCallbackManager.setBlockHandler(
                (exchange, t) ->
                        ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .body(fromObject(Result.tooManyRequest()))
        );
    }

    public static void registerRules(List<RouteDefinition> routes) {
        Set<GatewayFlowRule> rules = new HashSet<>();

        routes.forEach(route -> {
            String id = route.getId();

            rules.add(new GatewayFlowRule(id).setCount(100));

            rules.add(
                    new GatewayFlowRule(id)
                            .setCount(1)
                            .setParamItem(
                                    new GatewayParamFlowItem()
                                            .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
                            )
            );
        });

        GatewayRuleManager.register2Property(new DynamicSentinelProperty<>(rules));
    }

    @Autowired
    public void setGatewayProperties(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }*/
}
