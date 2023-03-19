package com.yrnet.spark.sparkbxadmin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.util.StopWatch;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@EnableFeignClients({
        "com.yrnet.spark.sparkbxadmin",
        "com.yrnet.spark.digitalaccountclient",
        "com.yrnet.spark.loyaltyfacadeclient"
})
@EnableCircuitBreaker
@ComponentScan("com.yrnet.spark")
@SpringBootApplication
@ComponentScan({
        "com.yrnet.spark.digitalaccountclient",
        "com.yrnet.spark.loyaltyfacadeclient"
})
public class SparkBxAdminApplication {

    private static final String SEPARATOR = "----------------------------------------------------------------";

    public static void main(String[] args) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ConfigurableApplicationContext context = SpringApplication.run(SparkBxAdminApplication.class);
        Environment env = context.getEnvironment();

        stopWatch.stop();
        final double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
        logApplicationStartup(env, totalTimeSeconds);
    }

    private static void logApplicationStartup(Environment env, double totalTimeSeconds) {

        if (env == null) {
            return;
        }

        final String serverPort = env.getProperty("server.port");
        final String contextPath = getContextPath(env);
        final String hostAddress = getHostAddress();
        final String isSecure = env.getProperty("server.ssl.enabled");

        String protocol = "http";
        if (Boolean.parseBoolean(isSecure)) {
            protocol = "https";
        }

        log.warn(SEPARATOR);
        log.warn("Started {} in {} seconds", StringUtils.upperCase(env.getProperty("spring.application.name")), totalTimeSeconds);
        log.warn(SEPARATOR);
        log.warn("Local: {}://localhost:{}{}", protocol, serverPort, contextPath);
        log.warn("External: {}://{}:{}{}", protocol, hostAddress, serverPort, contextPath);
        log.warn(SEPARATOR);
        log.warn("Keycloak: {}", env.getProperty("keycloak.auth-server-url"));
        log.warn("Digacc Api: {}", env.getProperty("com.yrnet.spark.end-points.digital-account-api-url"));
        log.warn("Digacc Process: {}", env.getProperty("com.yrnet.spark.end-points.digital-account-process-url"));
        log.warn("Hylo: {}", env.getProperty("com.yrnet.spark.end-points.hylo-url"));
        log.warn("Loyalty Facade: {}", env.getProperty("com.yrnet.spark.end-points.loyalty-facade.url"));
        log.warn("Audit: {}", env.getProperty("com.yrnet.spark.end-points.audit-api-url"));
        log.warn("Profile(s): {}", (Object) env.getActiveProfiles());
        log.warn(SEPARATOR);
    }

    private static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        return "localhost";
    }

    private static String getContextPath(Environment env) {
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        return contextPath;
    }

}
