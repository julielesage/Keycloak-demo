package com.yrnet.spark.sparkbxadmin.api.configuration;

import com.yrnet.spark.digitalaccountclient.exception.ApiException;
import com.yrnet.spark.sparkbxadmin.service.token.TokenService;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class ApiConfiguration {

    @Resource
    private TokenService tokenService;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    ErrorDecoder errorDecoder() {
        return (s, response) -> new ApiException(response.status(), getError(response));
    }

    private String getError(Response response) {
        StringWriter writer = new StringWriter();
        try {
            if (response.body() == null) {
                return response.toString();
            }
            IOUtils.copy(response.body().asInputStream(), writer, StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            return response.toString();
        }
        return writer.toString();
    }

    @Bean
    public RequestInterceptor addAccessTokenHeaderRequestInterceptor() {
        return template -> {
            try {
                template.header(AUTHORIZATION, Collections.singleton("Bearer " + tokenService.getAccessToken()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        };
    }

}
