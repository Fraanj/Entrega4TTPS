package ttps.proyecto.utils;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.Token;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import ttps.proyecto.services.TokenServices;

import java.io.IOException;

@WebFilter (filterName = "jwt-auth-filter", urlPatterns = "/*")
public class JWTAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest requestObject = (HttpServletRequest) request;
        if ("/api/auth".equals(requestObject.getRequestURI())||
                HttpMethod.OPTIONS.matches(requestObject.getRequestURI())){
            chain.doFilter(request, response);
            return;
        }

        String token  = requestObject.getHeader("HttpHeader.AUTHORIZATION");
        if (token == null || !TokenServices.validateToken(token)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
        chain.doFilter(request, response);
    }
}
