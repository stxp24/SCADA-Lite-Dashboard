package com.scada.monitoring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI scadaMonitoringOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SCADA Monitoring System API")
                        .description("REST API for industrial process monitoring - temperature, pressure, and motor state tracking with configurable threshold warnings. " +
                                "Supports real-time logging of multiple simultaneous sensor readings for trend analysis.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SCADA Development Team")
                                .email("support@scada-monitoring.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
