package ke.driveestate.controller;

import ke.driveestate.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @AuthenticationPrincipal ke.driveestate.model.User current, Model model) {
        if (current != null) return "redirect:/";
        if (error != null)  model.addAttribute("loginError", "Incorrect email or password.");
        if (logout != null) model.addAttribute("logoutMsg", "You have been logged out.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(@AuthenticationPrincipal ke.driveestate.model.User current) {
        if (current != null) return "redirect:/";
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam(required = false, defaultValue = "") String county,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           RedirectAttributes ra) {
        if (!password.equals(confirmPassword)) { ra.addFlashAttribute("error", "Passwords do not match."); return "redirect:/auth/register"; }
        if (password.length() < 8) { ra.addFlashAttribute("error", "Password must be at least 8 characters."); return "redirect:/auth/register"; }
        try {
            userService.register(name, email, phone, county, password);
            ra.addFlashAttribute("success", "Account created! Please log in.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register";
        }
    }
}
