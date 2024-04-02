package team.travel.travelplanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Configuration
public class AWSS3Config {

    @Bean
    public S3AsyncClient client() {
        return S3AsyncClient.crtBuilder()
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .build();
    }

    @Bean
    public S3TransferManager transferManager(S3AsyncClient client) {
        return S3TransferManager.builder()
                .s3Client(client)
                .build();
    }
}
