package com.talentstream.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    private static final String ENV_FIREBASE_SECRET = "FIREBASE_SERVICE_ACCOUNT_JSON";

    @PostConstruct
    public void initFirebase() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        String firebaseJson = System.getenv(ENV_FIREBASE_SECRET);
        if (!StringUtils.hasText(firebaseJson)) {
            throw new IllegalStateException(
                "Firebase secret is missing! Please set the environment variable: " + ENV_FIREBASE_SECRET
            );
        }

        try (InputStream serviceAccount = new ByteArrayInputStream(firebaseJson.getBytes())) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase initialized successfully from environment variable!");
        }
    }
}
