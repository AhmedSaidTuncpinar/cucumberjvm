package io.cucumber.junit.platform.engine;

import org.junit.platform.engine.ConfigurationParameters;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

class EmptyConfigurationParameters implements ConfigurationParameters {

    @Override
    public Optional<String> get(String key) {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        return Optional.empty();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<String> keySet() {
        return Collections.emptySet();
    }

}
