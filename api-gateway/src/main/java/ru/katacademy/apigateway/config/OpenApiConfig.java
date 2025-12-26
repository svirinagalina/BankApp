//package ru.katacademy.apigateway.config;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.servers.Server;
//import org.springdoc.core.models.GroupedOpenApi;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//public class OpenApiConfig {
//
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("Bank Application - API Gateway")
//                        .version("1.0")
//                        .description("""
//                            ## Unified API Documentation
//
//                            ### Available Services:
//                            * **Account Service** - API для управления банковскими счетами
//                            * **KYC Service** - Know Your Customer verification
//                            * **User Settings Service** - User preferences
//                            * **Fraud Detection Service** - Fraud monitoring
//                            * **Audit Service** - Audit logging
//                            * **Notification Service** - Notifications
//                            * **Auth Statistics Service** - Authentication stats
//
//                            ### Access:
//                            Use dropdown selector above to switch between services.
//                            """))
//                .servers(List.of(
//                        new Server()
//                                .url("/")
//                                .description("API Gateway (All services through gateway)")
//                ));
//    }
//
//    @Bean
//    public GroupedOpenApi gatewayApi() {
//        return GroupedOpenApi.builder()
//                .group("Gateway")
//                .pathsToMatch("/v3/api-docs/**", "/swagger-ui/**")
//                .build();
//    }
//}