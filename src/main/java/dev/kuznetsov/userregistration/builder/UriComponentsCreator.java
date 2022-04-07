package dev.kuznetsov.userregistration.builder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@PropertySource("classpath:uri-components.properties")
public class UriComponentsCreator {
    @Value("${uri.component.token.confirmation.scheme}")
    private String scheme;
    @Value("${uri.component.token.confirmation.port}")
    private int port;
    @Value("${uri.component.token.confirmation.host}")
    private String host;
    @Value("${uri.component.token.confirmation.path}")
    private String path;

    public UriComponents buildTokenConfirmationUri(String token) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .port(port)
                .host(host)
                .path(path)
                .queryParam("token", token)
                .build();
    }
}
