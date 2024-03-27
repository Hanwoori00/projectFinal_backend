package com.example.projectFinal;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
@EnableMongoAuditing
@EnableReactiveMongoAuditing
@SpringBootApplication
public class ProjectFinalApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(ProjectFinalApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}