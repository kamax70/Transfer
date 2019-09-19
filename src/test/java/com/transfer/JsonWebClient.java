package com.transfer;

import com.google.gson.Gson;
import com.transfer.controller.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.testng.Assert.assertEquals;

@Slf4j
public class JsonWebClient {

    private static CloseableHttpClient httpClient = HttpClients.custom().build();
    private static final Gson gson = new Gson();

    public static <T> T getForObject(String uri, Class<T> type, int code) {
        try {
            String content = get(uri, code);
            if (isBlank(content)) {
                return null;
            }
            return gson.fromJson(content, type);
        } catch (Throwable t) {
            log.error("Can't parse response: {}", t.toString());
            throw new RuntimeException(t);
        }
    }

    public static <T> T postForObject(String uri, Object request, Class<T> type, int code) {
        try {
            String content = post(uri, request, code);
            if (isBlank(content)) {
                return null;
            }
            return gson.fromJson(content, type);
        } catch (Throwable t) {
            log.error("Can't parse response: {}", t.toString());
            throw new RuntimeException(t);
        }
    }

    private static String get(String uri, int code) {
        return request(HttpMethod.GET, uri, null, code);
    }

    private static String post(String uri, Object request, int code) {
        String body = null;
        if (request != null) {
            body = gson.toJson(request);
        }
        return request(HttpMethod.POST, uri, body, code);
    }

    private static String request(HttpMethod method, String fullUrl, String body, int code) {
        log.debug("Requesting {} {} [{}]", method, fullUrl, body);
        try {
            HttpUriRequest httpRequest;
            if (method == HttpMethod.GET) {
                httpRequest = new HttpGet(fullUrl);
            } else if (method == HttpMethod.POST) {
                httpRequest = new HttpPost(fullUrl);
                if (body != null) {
                    ((HttpPost) httpRequest).setEntity(new StringEntity(body));
                }
            } else if (method == HttpMethod.PUT) {
                httpRequest = new HttpPut(fullUrl);
                if (body != null) {
                    ((HttpPut) httpRequest).setEntity(new StringEntity(body));
                }
            } else if (method == HttpMethod.DELETE) {
                httpRequest = new HttpDelete(fullUrl);
            } else {
                throw new IllegalStateException("unsupported HTTP method");
            }
            httpRequest.setHeader("Accept", "application/json");
            httpRequest.setHeader("Content-Type", "application/json");
            CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            assertEquals(statusCode, code);
            String response = IOUtils.toString(httpResponse.getEntity().getContent());
            if (statusCode == 200) {
                log.debug("200 OK: {}", response);
            } else {
                ErrorResponse errorResponse = gson.fromJson(response, ErrorResponse.class);
                log.debug("{} ERR: {} {}", statusCode, errorResponse.getErrorCode(), errorResponse.getErrorMessage());
            }
            return response;
        } catch (IOException e) {
            log.error("Exception during HTTP request", e);
            throw new RuntimeException(e);
        }
    }
}
