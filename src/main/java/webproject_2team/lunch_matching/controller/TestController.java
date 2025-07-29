package webproject_2team.lunch_matching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//================  HTML 샘플 불러오는 컨트롤러임. ================//
@Controller
public class TestController {

    @GetMapping("/test")
    public String showPage() {
        return "test"; // templates/test.html 로 변환
    }
}
