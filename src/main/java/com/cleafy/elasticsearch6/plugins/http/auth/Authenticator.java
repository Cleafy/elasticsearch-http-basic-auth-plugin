package com.cleafy.elasticsearch6.plugins.http.auth;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestRequest;

public abstract class Authenticator {
    private Settings settings;

    public Authenticator(Settings settings) {
        this.settings = settings;
    }

    public abstract boolean authenticate(RestRequest request);
}
