package io.kentec.SaccoMobile.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;

@Service
@Transactional
public class MpesaToken {

    @Autowired
    private Environment environment;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(MpesaToken.class);

    public static String token = "";

    @PostConstruct
    public void init() {
        try {
            autoSetOauthBearerCode();
            LOGGER.info("BearerTokenService init token={}", token);
        } catch (Exception e) {
            LOGGER.error("Error when initializing BearerTokenService for token fetch={}", e);
        }
    }

    @Scheduled(initialDelay = 3000, fixedDelay = 1000 * 60 * 30)
    public void autoSetOauthBearerCode() {
        try {
            String accessToken = generateAccessToken(environment);
            token = accessToken;
            LOGGER.info("Refreshed token={}", token);
        } catch (Exception e) {
            LOGGER.error("Error fetching token ={}", e);
        }
    }

    private String generateAccessToken(Environment environment) {
        String accessToken = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String authUrl = environment.getProperty("mpesa.auth_url");
            String customerKey = environment.getRequiredProperty("mpesa.consumer_key");
            String customerSecret = environment.getRequiredProperty("mpesa.consumer_secret");

            String encryptByte = customerKey + ":" + customerSecret;
            // encode with padding
            String encoded = Base64.getEncoder().encodeToString(encryptByte.getBytes());
            String authorizationString = "Basic " + encoded;

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization", authorizationString);
            headers.add("Access-Control-Allow-Origin", "*");

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            LOGGER.info("auth url: " + authUrl + ", request : " + requestEntity.toString());

            ResponseEntity<String> response = restTemplate.exchange(authUrl, HttpMethod.GET, requestEntity, String.class);
            JSONObject authResponse = new JSONObject(response.getBody());
            LOGGER.info("authJsonResponse >>> " + authResponse);

            if (authResponse.toString().toUpperCase().contains("ACCESS")) {
                accessToken = authResponse.getString("access_token");
                LOGGER.info("access_token : " + accessToken);
                String expires_in = authResponse.getString("expires_in");
                LOGGER.info("expires_in : " + expires_in);
            }
        } catch (RestClientException | JSONException e) {
            LOGGER.info("Encountered error in bearer authentication : " + e.getMessage());
        }
        return accessToken;
    }

}
