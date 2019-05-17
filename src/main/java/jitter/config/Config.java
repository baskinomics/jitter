package jitter.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the YAML configuration file provided by the user.
 */
public class Config {

    /**
     * Collection of {@code String} instances representing the path a git repository on disk.
     */
    private List<String> repositories = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Config() {

    }

    /**
     * Returns an immuatble collection representing the {@code repositories} list.
     *
     * @return The immuatble collection representing the {@code repositories} list.
     */
    public List<String> getRepositories() {
        return Collections.unmodifiableList(this.repositories);
    }

    /**
     * Clears {@link this#repositories} and adds the given {@code repositories}.
     *
     * @param repositories The repositories to set.
     */
    public void setRepositories(final ArrayList<String> repositories) {
        this.repositories.clear();
        this.repositories.addAll(repositories);
    }

}