package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Request configuration mapper.
 */
final class ConfigMapper {

    private final ModelMapper mapper = new ModelMapper();

    ConfigMapper() {
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.getConfiguration().setCollectionsMergeEnabled(false);
    }

    public RequestConfig getValuesFromConfigFile(final String yamlData) {
        final var modelMapper = new ModelMapper();

        final var propertyMap = new PropertyMap<YamlConfiguration, RequestConfig>() {
            @Override
            protected void configure() {
                // Map headers
                final Converter<Map<String, String>, List<Header>> headersConverter = ctx -> {
                    if (ctx.getSource() == null) {
                        return new ArrayList<>();
                    }
                    return ctx.getSource().entrySet().stream()
                            .map(entry -> new Header(entry.getKey(), entry.getValue())).collect(Collectors.toList());
                };
                using(headersConverter).map(source.getHeaders()).setHeaders(null);
                // Map response length
                final Converter<Integer, Integer> respConverter = ctx -> ctx.getSource() == null
                        ? Integer.valueOf(0) : ctx.getSource();
                using(respConverter).map().setMaxResponseLength(source.getMaxResponseLength());
                // Map timeout
                map().setRequestTimeOut(source.getConnectionTimeout());
            }
        };

        final var yamlConfiguration = readYamlConfiguration(yamlData);

        return modelMapper.addMappings(propertyMap).map(Objects.requireNonNull(yamlConfiguration));
    }

    private YamlConfiguration readYamlConfiguration(final String in) {
        if (Objects.isNull(in)) {
            return new YamlConfiguration();
        }

        return new Yaml().loadAs(in, YamlConfiguration.class);
    }

    public RequestConfig getValuesFromConfigObject(final RequestConfig config) {
        return new ModelMapper().map(config, RequestConfig.class);
    }

    public RequestConfig getValuesFromBuilder(final RequestBuilder requestBuilder) {
        final var modelMapper = new ModelMapper();

        final var propertyMap = new PropertyMap<RequestBuilder, RequestConfig>() {
            @Override
            protected void configure() {
                map().setExpectedStatusCode(source.getStatusCode());
            }
        };

        return modelMapper.addMappings(propertyMap).map(requestBuilder);
    }

    public void map(final RequestConfig source, final RequestConfig destination) {
        mapper.map(source, destination);
    }
}
