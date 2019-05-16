package jitter.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Config {
    
    private List<String> repositories = new ArrayList<>();
    
    /**
     * 
     */
    public Config() {

    }

    /**
     * 
     * @return
     */
    public List<String> getRepositories() {
        return this.repositories;
    }

    /**
     * 
     * @param repositories
     */
    public void setRepositories(final ArrayList<String> repositories) {
        this.repositories.clear();
        this.repositories.addAll(repositories);
    }

}