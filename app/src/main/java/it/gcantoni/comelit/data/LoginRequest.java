package it.gcantoni.comelit.data;

/**
 * Data model for the authentication request.
 * Encapsulates credentials sent to the login endpoint.
 */
public class LoginRequest {

    // Access modifiers changed to private to follow encapsulation principles
    private String username;
    private String password;

    /**
     * Constructs a new LoginRequest with the required credentials.
     *
     * @param username The user identifier (e.g., "tester")
     * @param password The secret key provided for API access
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Standard Getters (Useful for debugging or if logic is moved to a Repository)
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}