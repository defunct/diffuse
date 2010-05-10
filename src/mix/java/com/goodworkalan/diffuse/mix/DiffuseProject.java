package com.goodworkalan.diffuse.mix;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.builder.JavaProject;

public class DiffuseProject extends ProjectModule {
    @Override
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.diffuse/diffuse/0.1.0.1")
                .main()
                    .depends()
                        .include("com.github.bigeasy.danger/danger/0.1")
                        .include("com.github.bigeasy.class-association/class-association/0.1")
                        .end()
                    .end()
                .test()
                    .depends()
                        .include("org.testng/testng-jdk15/5.10")
                        .include("org.mockito/mockito-core/1.6")
                        .end()
                    .end()
                .end()
            .end();
    }
}
