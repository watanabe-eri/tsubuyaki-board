package com.example.tsubuyaki;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class TsubuyakiApplicationMainTest {

    @Test
    @DisplayName("アプリ起動_main実行時_SpringApplicationを起動する")
    void アプリ起動_main実行時_SpringApplicationを起動する() {
        String[] args = {"--spring.profiles.active=h2"};

        try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
            TsubuyakiApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(TsubuyakiApplication.class, args));
        }
    }
}
