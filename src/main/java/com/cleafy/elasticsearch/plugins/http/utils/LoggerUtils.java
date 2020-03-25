package com.cleafy.elasticsearch.plugins.http.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.rest.RestRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class LoggerUtils {

    public static void logRequest(final RestRequest request, Class<?> klass) {
        String addr = getAddress(request).getHostAddress();
        String t = "Authorization:{}, type: {}, Host:{}, Path:{}, {}:{}, Request-IP:{}, " +
                "Client-IP:{}, X-Client-IP{}";
        Logger log = LogManager.getLogger(klass);

        log.info(t,
                request.header("Authorization"),
                request.method(),
                request.header("Host"),
                request.path(),
                addr,
                request.header("X-Client-IP"),
                request.header("Client-IP"));
    }

    public static void logUnAuthorizedRequest(final RestRequest request, Class<?> klass) {
        String addr = getAddress(request).getHostAddress();
        String t = "UNAUTHORIZED type:{}, address:{}, path:{}, request:{}, content:{}";
        Logger log = LogManager.getLogger(klass);

        log.error(t,
                request.method(), addr, request.path(), request.params(),
                request.content().utf8ToString());
    }

    private static InetAddress getAddress(RestRequest request) {
        return ((InetSocketAddress) request.getHttpChannel().getRemoteAddress()).getAddress();
    }
}
