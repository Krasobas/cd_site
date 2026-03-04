package ru.job4j.site.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class RestAuthCall {
    private final RestTemplate restTemplate;

    public String get(String url) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(headers), new ParameterizedTypeReference<String>() {
                }
        ).getBody();
    }

    public String get(String url, String token) {
        restTemplate.setErrorHandler(
                new DefaultResponseErrorHandler() {
                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        var respValue = response.getStatusCode().value();
                        if (respValue != 401 && respValue != 404) {
                            log.error("Call: " + url, response.getStatusText());
                        }
                    }
                }
        );
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + token);
        return restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(headers), new ParameterizedTypeReference<String>() {
                }
        ).getBody();
    }

    public String getWithHeaders(String url, HttpHeaders headers) {
        restTemplate.setErrorHandler(
                new DefaultResponseErrorHandler() {
                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        var respValue = response.getStatusCode().value();
                        if (respValue != 401 && respValue != 404) {
                            log.error("Call: " + url, response.getStatusText());
                        }
                    }
                }
        );
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(headers), new ParameterizedTypeReference<String>() {
                }
        ).getBody();
    }

    public String token(String url, Map<String, String> params) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic am9iNGo6cGFzc3dvcmQ=");
        var map = new LinkedMultiValueMap<String, String>();
        params.forEach(map::add);
        map.add("scope", "any");
        map.add("grant_type", "password");
        return restTemplate.postForEntity(
                url, new HttpEntity<>(map, headers), String.class
        ).getBody();
    }

    public String post(String url, String token, String json) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        return restTemplate.postForEntity(
                url, new HttpEntity<>(json, headers), String.class
        ).getBody();
    }

    public void update(String url, String token, String json) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        var request = new HttpEntity<>(json, headers);
        restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
    }

    public void delete(String url, String token, String json) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        var request = new HttpEntity<>(json, headers);
        restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
    }

    public void put(String url, String token, String json) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        restTemplate.put(
                url, new HttpEntity<>(json, headers), String.class
        );
    }
}
