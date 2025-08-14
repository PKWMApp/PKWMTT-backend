package test;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class TestConfig {

    protected static final int WIREMOCK_PORT = 9999;

    @RegisterExtension
    protected static final WireMockExtension EXTERNAL_SERVICE_API_MOCK = WireMockExtension.newInstance()
            .options(wireMockConfig().port(WIREMOCK_PORT)).build();
}
