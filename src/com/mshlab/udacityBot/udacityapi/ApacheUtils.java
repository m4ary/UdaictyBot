package com.mshlab.udacityBot.udacityapi;


import com.mshlab.udacityBot.udacityapi.model.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;


import java.io.IOException;

final class ApacheUtils {
    private ApacheUtils() {

    }


    private static Request getRequest(String method, UdacityAPI.REQUEST_TYPE request_type, String key, String params) {
        Request request;
        switch (request_type) {
            case POST:
                request = Request.Post(method);
                if (params != null) {
                    request.bodyString(params, ContentType.APPLICATION_JSON);
                }
                break;
            case DELETE:
                request = Request.Delete(method);
                if (params != null) {
                    request.bodyString(params, ContentType.APPLICATION_JSON);
                }
                break;
            case PUT:
                request = Request.Options(method);
                if (params != null) {
                    request.bodyString(params, ContentType.APPLICATION_JSON);
                }
                break;
            case GET:
            default:
                if (!TextUtils.isEmpty(method)) {
                    request = Request.Get(method);
                } else {
                    request = Request.Get(method + "?" + params);

                }
                break;
        }
        return request
                .addHeader("Host", "review-api.udacity.com")
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:76.0) Gecko/20100101 Firefox/76.0")
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language:", "en-US,en;q=0.5")
                .addHeader("Accept-Encoding", "application/json")
                .addHeader("Content-Type", " application/json")
                .addHeader("Authorization", "Bearer " + key)
                .addHeader("Origin", "https://mentor-dashboard.udacity.com")
                .addHeader("Referer", "https://mentor-dashboard.udacity.com/queue/overview");


    }

    public static Response getResponse(String method, UdacityAPI.REQUEST_TYPE requestType, String key, String params) throws IOException {
        HttpResponse httpResponse = getRequest(method, requestType, key, params).execute().returnResponse();
        Response response = new Response();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        if (httpResponse.getEntity() == null) {
            return response;
        }
        response.setContent(EntityUtils.toString(httpResponse.getEntity()));
        return response;
    }
}
