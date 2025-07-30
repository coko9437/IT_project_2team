package webproject_2team.lunch_matching.controller; // 이 패키지 선언이 정확해야 합니다.

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appName", "Lunch Match Application");
        model.addAttribute("pageLocation", "src/main/resources/templates/index.html");
        model.addAttribute("currentTime", LocalDateTime.now());
        return "index";
    }
}