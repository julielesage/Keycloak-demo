package com.yrnet.spark.sparkbxadmin.helper;

import com.yrnet.spark.sparkbxadmin.properties.ServerProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FeatureFlagHelper {

    protected static final String FIDBACK_ENABLE = "fidback-enable";

    private final ServerProperties serverProperties;

    public FeatureFlagHelper(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    public boolean isLoyaltyEnabled(String realm) {
        if (realm == null) {
            return false;
        }

        final Map<String, String> options = serverProperties.getCountries().get(realm);
        if (options == null) {
            return false;
        }

        return options.containsKey(FIDBACK_ENABLE) && Boolean.parseBoolean(options.get(FIDBACK_ENABLE));
    }

}
