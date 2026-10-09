package com.ashlesh.mycoolproject.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // This bean sets the title, version and description shown at the top of Swagger UI
    @Bean
    public OpenAPI foodOrderingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Food Ordering API")
                        .version("1.0")
                        .description("REST APIs for the Food Ordering application: "
                                + "restaurants, food items, customers, carts and orders."));
    }
}
