//package ru.katacademy.bank_app.accountservice.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//
//@Configuration
//public class CorsConfig {
//
//    private final Environment env;
//
//    public CorsConfig(Environment env) {
//        this.env = env;
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        final CorsConfiguration config = new CorsConfiguration();
//
//        // Разрешённые источники (зависит от профиля)
//        if (isProdProfile()) {
//            config.setAllowedOrigins(List.of(
//                    "https://frontend1.example.com",
//                    "https://frontend2.example.com"
//            ));
//        } else {
//            config.setAllowedOrigins(List.of(
//                    "http://localhost:3000",
//                    "http://localhost:8080"
//            ));
//        }
//
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
//        config.setAllowCredentials(true);
//        config.setMaxAge(3600L); // 1 час
//
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//}
//
//    private boolean isProdProfile() {
//        final String[] profiles = env.getActiveProfiles();
//        return profiles.length > 0 && "prod".equalsIgnoreCase(profiles[0]);
//    }
//}