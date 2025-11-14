package ttps.proyecto.dto;


public class AuthResponse {
    private String token;
    private int expirationTime;
    private String userEmail;
    public AuthResponse(String token, int expirationTime, String userEmail) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.userEmail = userEmail;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public int getExpirationTime() {
        return expirationTime;
    }
    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

