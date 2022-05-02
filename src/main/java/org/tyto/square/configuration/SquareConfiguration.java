package org.tyto.square.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "square")
public class SquareConfiguration {

    private String appId;
    private String appSecret;
    private List<String> appScopes;
    private SquareEnvironmentConfig environment;
    private Long stateIdTtl;
}
