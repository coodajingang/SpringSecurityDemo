package demo.ss.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FailureLoginHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("Failure on login", exception);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("msg", exception.getMessage());

        ObjectMapper mapper = new ObjectMapper();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(403);
        response.getWriter().write(mapper.writeValueAsString(map));
        response.getWriter().flush();
    }
}
