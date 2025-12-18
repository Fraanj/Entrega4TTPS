package ttps.proyecto.dto;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UsuarioDto usuario;
    private Long expiresIn;

    // Constructor con 4 parámetros (compatible con la versión anterior)
    public AuthResponse(String accessToken, String refreshToken, UsuarioDto usuario, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.usuario = usuario;
        this.expiresIn = expiresIn;
    }

    // Constructor simplificado con 2 parámetros (NUEVO)
    public AuthResponse(String accessToken, UsuarioDto usuario) {
        this.accessToken = accessToken;
        this.refreshToken = null; // Sin refresh token por ahora
        this.usuario = usuario;
        this.expiresIn = 86400L; // 24 horas en segundos
    }

    // Getters y setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public UsuarioDto getUsuario() { return usuario; }
    public void setUsuario(UsuarioDto usuario) { this.usuario = usuario; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}
