package ttps.proyecto.services;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class TokenServices {
    final static Key key;
    static {
        try {
            key = KeyGenerator.getInstance("HmacSHA256").generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String username, int segundos) {
        Date exp = getExpiration(new Date(), segundos);
        return Jwts.builder().setSubject(username).signWith(key).setExpiration(exp).compact();
    }

    private Date getExpiration(Date now, int segundos) {
        return new Date(now.getTime() + segundos * 1000);
    }

    public static boolean validateToken(String token) {
        String prefix = "Bearer ";
        try {
            if(token.startsWith(prefix)){
                token = token.substring(prefix.length()).trim();
            }
            Claims claims = Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch(ExpiredJwtException e){
            return false;
        } catch (JwtException e){
            return false;
        }
    }
}
