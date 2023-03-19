package com.yrnet.spark.sparkbxadmin.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties("com.yrnet.spark.sparkbxadmin")
public class ServerProperties {

    private Map<String, Map<String, String>> countries = new HashMap<>();

}
