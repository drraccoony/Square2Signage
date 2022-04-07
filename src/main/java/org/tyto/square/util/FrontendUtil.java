package org.tyto.square.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.tyto.square.configuration.FrontendConfiguration;

@Service
public class FrontendUtil {

    @Autowired
    FrontendConfiguration config;

    private String buildUriString(String path) {
        return UriComponentsBuilder
                .newInstance()
                .scheme(config.getHttpScheme())
                .host(config.getHost())
                .path(path)
                .build()
                .toString();
    }

    public String getSuccessUri() {
        return buildUriString(config.getSuccessPath());
    }

    public String getFailureUri() {
        return buildUriString(config.getFailurePath());
    }
}
