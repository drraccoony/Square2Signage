package org.tyto.square.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "frontend")
public class FrontendConfiguration {
    private String host;
    private String httpScheme;
    private String failurePath;
    private String successPath;
}
