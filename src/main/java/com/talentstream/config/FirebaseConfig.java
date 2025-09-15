package com.talentstream.config;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() throws IOException {
        String renderPath = "/etc/secrets/firebase-services-account.json";
        String localPath = "src/main/resources/firebase-services-account.json";

        String pathToUse;

        if (new File(renderPath).exists()) {
            pathToUse = renderPath; 
        } else {
            pathToUse = localPath;  
        }

        FileInputStream serviceAccount = new FileInputStream(pathToUse);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}

