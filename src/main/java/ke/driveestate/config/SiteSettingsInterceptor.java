package ke.driveestate.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ke.driveestate.service.SiteSettingService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Injects site settings into every page's model so templates can use
 * ${settings['contact_phone']} etc. without each controller fetching them.
 */
@Component
public class SiteSettingsInterceptor implements HandlerInterceptor {

    private final SiteSettingService settingService;

    public SiteSettingsInterceptor(SiteSettingService settingService) {
        this.settingService = settingService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView mav) {
        if (mav != null && !response.isCommitted()) {
            try {
                mav.addObject("settings", settingService.getAll());
            } catch (Exception ignored) {
                // DB not ready yet — safe to ignore during startup
            }
        }
    }
}
