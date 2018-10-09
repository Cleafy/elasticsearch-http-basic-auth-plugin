package com.cleafy.elasticsearch6.plugins.http.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HTTPHelper {
    public static AuthCredentials extractCredentials(String authorizationHeader) {

        if (authorizationHeader != null) {
            if (!authorizationHeader.trim().toLowerCase().startsWith("basic ")) {
                return null;
            } else {

                final String decodedBasicHeader = new String(Base64.getDecoder().decode(authorizationHeader.split(" ")[1]),
                        StandardCharsets.UTF_8);

                //username:password
                //special case
                //username must not contain a :, but password is allowed to do so
                //   username:pass:word
                //blank password
                //   username:

                final int firstColonIndex = decodedBasicHeader.indexOf(':');

                String username = null;
                String password = null;

                if (firstColonIndex > 0) {
                    username = decodedBasicHeader.substring(0, firstColonIndex);

                    if (decodedBasicHeader.length() - 1 != firstColonIndex) {
                        password = decodedBasicHeader.substring(firstColonIndex + 1);
                    } else {
                        //blank password
                        password = "";
                    }
                }

                if (username == null || password == null) {
                    return null;
                } else {
                    return new AuthCredentials(username, password.getBytes(StandardCharsets.UTF_8)).markComplete();
                }
            }
        } else {
            return null;
        }
    }
}
