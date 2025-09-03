package co.com.loanapplications.authenticationclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "authentication.client")
public class AuthenticationClientProperties {
    private String baseUrl = "http://localhost:8080";
    private String existsPath = "/api/v1/users/exists/{email}";
    private int connectTimeoutMs = 3000;
    private int readTimeoutSec = 3;
}
