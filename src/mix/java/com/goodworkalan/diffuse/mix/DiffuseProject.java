package com.goodworkalan.diffuse.mix;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.cookbook.JavaProject;

/**
 * Builds the project definition for Diffuse.
 *
 * @author Alan Gutierrez
 */
public class DiffuseProject implements ProjectModule {
    /**
     * Build the project definition for Diffuse.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.diffuse/diffuse/0.1.0.7")
                .depends()
                    .production("com.github.bigeasy.reflective/reflective-getter/0.+1")
                    .production("com.github.bigeasy.class/class-association/0.+1")
                    .development("org.testng/testng-jdk15/5.10")
                    .end()
                .end()
            .end();
    }
}
