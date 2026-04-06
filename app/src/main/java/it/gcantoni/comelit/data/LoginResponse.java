package it.gcantoni.comelit.data;

import com.google.gson.annotations.SerializedName;

/**
 * Data model for the authentication response.
 * Contains the Bearer token required for subsequent API requests.
 */
public class LoginResponse {

    @SerializedName("access_token")
    private String accessToken;

    /**
     * Retrieves the JWT or Bearer token provided by the server.
     *
     * @return The access token string, or null if the login failed.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Helper method to format the token for the Authorization header.
     *
     * @return The formatted Bearer token string.
     */
    public String getAuthorizationHeader() {
        return (accessToken != null) ? "Bearer " + accessToken : null;
    }
}