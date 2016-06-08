package com.tudorluca.sandbox.okhttp;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by tudor on 29/04/16.
 */
public class UserAuthModel {

    private String username;
    private String password;
    private String client_id;
    private String client_secret;
    private String scope;
    private String grant_type;

    public UserAuthModel(String username, String password, String clientId, String clientSecret, String scope, String grantType) {
        this.username = username;
        this.password = password;
        this.client_id = clientId;
        this.client_secret = clientSecret;
        this.scope = scope;
        this.grant_type = grantType;
    }

    public Map<String, String> toFieldMap() {
        return new ImmutableMap.Builder<String, String>()
                .put("username", username)
                .put("password", password)
                .put("client_id", client_id)
                .put("client_secret", client_secret)
                .put("grant_type", grant_type)
                .put("scope", scope)
                .build();
    }

    public static UserAuthModel withUsernameAndPassword(final String username, final String password) {
        return new Builder()
                .setUsername(username)
                .setPassword(password)
                .setClientId("")
                .setClientSecret("")
                .setGrantType("")
                .setScope("")
                .build();
    }

    public static class Builder {
        private String mUsername;
        private String mPassword;
        private String mClientId;
        private String mClientSecret;
        private String mScope;
        private String mGrantType;

        public Builder setUsername(String username) {
            mUsername = username;
            return this;
        }

        public Builder setPassword(String password) {
            mPassword = password;
            return this;
        }

        public Builder setClientId(String clientId) {
            mClientId = clientId;
            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            mClientSecret = clientSecret;
            return this;
        }

        public Builder setScope(String scope) {
            mScope = scope;
            return this;
        }

        public Builder setGrantType(String grantType) {
            mGrantType = grantType;
            return this;
        }

        public UserAuthModel build() {
            return new UserAuthModel(mUsername, mPassword, mClientId, mClientSecret, mScope, mGrantType);
        }
    }
}

