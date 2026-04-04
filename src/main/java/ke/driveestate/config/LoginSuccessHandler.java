package ke.driveestate.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ke.driveestate.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Routes each user to their correct portal after login:
 *   ADMIN  → /admin/dashboard
 *   CLIENT → /dashboard
 *
 * Clears Spring's saved-request so visiting /admin before login
 * never causes a 403 Forbidden redirect after login.
 *
 * Works for unlimited simultaneous sessions — each browser/tab
 * gets its own independent session cookie.
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        // Remove any Spring-saved "redirect after login" URL — prevents 403 loops
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
        }

        // Route based on role
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
