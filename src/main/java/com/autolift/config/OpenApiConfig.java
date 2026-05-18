package com.autolift.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("AutoLift Loyalty API")
                .version("0.0.1-SNAPSHOT")
                .description("Uplift Modeling + Loyalty Promotion System")
                .contact(
                    new Contact()
                        .name("AutoLift Team")
                        .url("https://autolift.example.com"))
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
        .servers(List.of(new Server().url("http://localhost:8080").description("Local Dev Server")));
  }
}