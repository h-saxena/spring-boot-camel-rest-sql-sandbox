package io.fabric8.quickstarts.camel.config.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DbConfig {
    
    @Value("${crmp.datasource.username}")
    private String username;
    
    @Value("${crmp.datasource.password}")
    private String password;

    @Value("${crmp.datasource.url}")
    private String url;

    @Value("${crmp.datasource.driver-class-name}")
    private String driverClassName;

    public String getUsername() {
        return username;
    }

    public String getUrl() {
        return url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    
    public String getPassword() {
        return password;
    }

}
