package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configures request.
 */
public class RequestDirector {

    private final RequestConfig requestConfig = new RequestConfig();
    private final ConfigMapper mapper = new ConfigMapper();
    private String yamlConfigFile;

    public void setYamlConfigFile(final String yamlConfigFile) {
        this.yamlConfigFile = yamlConfigFile;
    }

    public RequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    public void constructRequest(final RequestBuilder requestBuilder) {
        // Load values from yaml config file
        final var fileConfig = mapper.getValuesFromConfigFile(getDataFromYamlFile(yamlConfigFile));
        mapper.map(fileConfig, requestConfig);

        // Get values from passed request configuration
        var builderConfig = new RequestConfig();
        if (requestBuilder.getConfig() != null) {
            builderConfig = mapper.getValuesFromConfigObject(requestBuilder.getConfig());
            mapper.map(builderConfig, requestConfig);
        }

        // Get values from passed arguments
        final var argumentsConfig = mapper.getValuesFromBuilder(requestBuilder);
        mapper.map(argumentsConfig, requestConfig);

        // Merge headers
        requestConfig.setHeaders(mergeHeaders(fileConfig, builderConfig, argumentsConfig));

        // Add authorization
        setToken(requestBuilder);
    }

    private List<Header> mergeHeaders(final RequestConfig fileConfig, final RequestConfig builderConfig,
                                      final RequestConfig argumentsConfig) {
        final Function<RequestConfig, List<Header>> extractHeaders = config -> config.getHeaders() == null
                ? List.of() : config.getHeaders();

        final var fileHeaders = extractHeaders.apply(fileConfig);
        final var builderHeaders = extractHeaders.apply(builderConfig);
        final var argHeaders = extractHeaders.apply(argumentsConfig);

        final var headers = new ArrayList<Header>();
        if (builderHeaders.isEmpty()) {
            headers.addAll(fileHeaders);
        }
        headers.addAll(builderHeaders);
        headers.addAll(argHeaders);

        return headers;
    }

    private String getDataFromYamlFile(final String fileName) {
        if (Objects.isNull(fileName) || Objects.isNull(getClass().getClassLoader().getResource(fileName))) {
            return null;
        }

        final var url = getClass().getClassLoader().getResource(fileName);
        Stream<String> lines = null;
        try {
            final var path = Paths.get(Objects.requireNonNull(url, "yaml config not found").toURI());
            lines = Files.lines(path);
        } catch (IOException | URISyntaxException error) {
            final var log = LoggerFactory.getLogger(this.getClass());
            log.error(error.getMessage());
        }

        return Objects.requireNonNull(lines).collect(Collectors.joining("\n"));
    }

    private void setToken(final RequestBuilder requestBuilder) {
        if (requestBuilder.getToken() != null) {
            requestConfig.getHeaders().add(new Header("Authorization", requestBuilder.getToken()));
        }
    }
}
