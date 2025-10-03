package org.os.bayturabackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class DotenvTestConfig {
    static {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    }
}
