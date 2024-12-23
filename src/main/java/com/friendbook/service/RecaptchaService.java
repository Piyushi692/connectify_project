package com.friendbook.service;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class RecaptchaService {

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String SECRET_KEY = "6LeYNigqAAAAAJdF3Ot6roIai8ILZ6okeZHvzF1Q";
    RestTemplate restTemplate = new RestTemplate();

    public RecaptchaService() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(10000);  // 10 seconds
        clientHttpRequestFactory.setReadTimeout(10000);
        this.restTemplate = new RestTemplate(clientHttpRequestFactory);
    }

    public boolean verifyRecaptcha(String recaptchaResponse) {


        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(RECAPTCHA_VERIFY_URL)
            .queryParam("secret", SECRET_KEY)
            .queryParam("response", recaptchaResponse);

        Map<String, Object> response = restTemplate.postForObject(uriBuilder.toUriString(), null, HashMap.class);

        return response != null && (Boolean) response.get("success");
    }
}
