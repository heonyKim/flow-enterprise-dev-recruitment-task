package com.heony.flowteam.enterprisedevrecruitmenttask._common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){


        return new OpenAPI()
                .info(new Info()
                        .title("Flow Team Enterprise Dev Recruitment Task"))
                .addServersItem(new Server().url("/"));
    }
}
