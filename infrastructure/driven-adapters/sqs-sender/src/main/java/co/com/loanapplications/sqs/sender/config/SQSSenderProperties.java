package co.com.loanapplications.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.sqs")
public record SQSSenderProperties(
        String region,
        String publishQueueUrl,
        String validateQueueUrl,
        String publishReportQueueUrl
) {
}
