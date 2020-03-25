package com.cleafy.elasticsearch.plugins.http;

import com.cleafy.elasticsearch.plugins.http.auth.AuthCredentials;
import com.cleafy.elasticsearch.plugins.http.auth.HttpBasicAuthenticator;
import com.cleafy.elasticsearch.plugins.http.utils.Globals;
import com.cleafy.elasticsearch.plugins.http.utils.LoggerUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;
import org.elasticsearch.transport.TransportException;

public class BasicRestFilter {
    private final HttpBasicAuthenticator httpBasicAuthenticator;
    private boolean isUnauthLogEnabled;

    public BasicRestFilter(final Settings settings) {
        super();
        this.httpBasicAuthenticator = new HttpBasicAuthenticator(settings, new AuthCredentials(settings.get(Globals.SETTINGS_USERNAME, "pippo"), settings.get(Globals.SETTINGS_PASSWORD, "pluto").getBytes()));
        this.isUnauthLogEnabled = settings.getAsBoolean(Globals.SETTINGS_LOG, false);
    }

    public RestHandler wrap(RestHandler original) {
        return (request, channel, client) -> {
            if (!checkAndAuthenticateRequest(request, channel, client)) {
                original.handleRequest(request, channel, client);
            }
        };
    }

    private boolean checkAndAuthenticateRequest(RestRequest request, RestChannel channel, NodeClient client) throws Exception {
        ElasticsearchException forbiddenException = new TransportException("Forbidden");
        try {
            if (this.httpBasicAuthenticator.authenticate(request)) {
                LoggerUtils.logRequest(request, getClass());
                return false;
            }

            if (this.isUnauthLogEnabled) { LoggerUtils.logUnAuthorizedRequest(request, getClass()); }
            channel.sendResponse(new BytesRestResponse(channel, RestStatus.FORBIDDEN, forbiddenException));
        } catch (Exception e) {
            if (this.isUnauthLogEnabled) { LoggerUtils.logUnAuthorizedRequest(request, getClass()); }
            channel.sendResponse(new BytesRestResponse(channel, RestStatus.FORBIDDEN, forbiddenException));
            return true;
        }
        return true;
    }
}
