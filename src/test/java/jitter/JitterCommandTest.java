package jitter;

import jitter.domain.model.Config;
import org.junit.jupiter.api.Test;

public class JitterCommandTest {

    /**
     * Tests the instantiation of a {@link Config} instance.
     */
    @Test
    public void testCreateConfig() {
        final var config = new Config();
        assert config.getRepositories().isEmpty();
    }

    /**
     * Tests the instantiation of a {@link Config} instance from a file.
     */
    @Test
    public void testLoadConfigFromFile() {
        assert false;
    }

}
