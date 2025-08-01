package webproject_2team.lunch_matching.controller.signup;

import webproject_2team.lunch_matching.domain.signup.Member;
import webproject_2team.lunch_matching.dto.signup.MemberResponseDTO;
import webproject_2team.lunch_matching.service.signup.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // AuthenticationPrincipal 임포트
import org.springframework.security.core.userdetails.User; // Spring Security의 User 객체 임포트
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MyPageController {

    private final MemberService memberService;

    @GetMapping("/myPage")
    public String myPage(@AuthenticationPrincipal User user, Model model) {
        // @AuthenticationPrincipal User user: Spring Security가 현재 로그인된 사용자의 기본 정보를 User 객체로 제공합니다.
        // 이 User 객체는 사용자 ID (getUsername()), 역할 등을 포함합니다.
        // 실제로는 UserDetailsService에서 반환하는 객체 (예: MemberUserDetails)를 직접 받을 수도 있습니다.

        if (user != null) {
            String username = user.getUsername(); // 로그인된 사용자의 아이디 (username)
            log.info("Accessing myPage for user: {}", username);

            try {
                // MemberService를 통해 username으로 MemberResponseDTO 조회
                // MemberService의 getMemberByUsername 메서드가 MemberResponseDTO를 반환하도록 수정되었음을 전제로 합니다.
                MemberResponseDTO memberResponseDTO = memberService.getMemberByUsername(username);
                // 조회된 MemberResponseDTO 객체를 모델에 추가하여 HTML로 전달
                model.addAttribute("member", memberResponseDTO);
                log.info("Member data added to model for myPage: Nickname={}, ProfileThumbnailUrl={}",
                        memberResponseDTO.getNickname(), memberResponseDTO.getProfileThumbnailUrl() != null ? memberResponseDTO.getProfileThumbnailUrl() : "N/A");

            } catch (IllegalArgumentException e) {
                // 해당 아이디의 회원을 찾을 수 없는 경우 (비정상적인 상황이지만 대비)
                log.error("Error finding member for myPage: {}", e.getMessage());
                // 에러 페이지로 리다이렉트하거나 오류 메시지를 모델에 추가할 수 있습니다.
                return "redirect:/errorPage"; // 예시: 에러 페이지로 리다이렉트
            }
        } else {
            // user가 null인 경우 (로그인되지 않은 상태).
            // Spring Security 설정에 따라 /myPage 접근 시 /login으로 리다이렉트되므로,
            // 이 블록은 거의 실행되지 않을 것입니다.
            log.warn("Unauthorized access to myPage. Redirecting to login.");
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        return "myPage"; // src/main/resources/templates/myPage.html 템플릿 렌더링
    }

    // 내 정보 수정 페이지 렌더링
    @GetMapping("/myPage/edit")
    public String editMyPage(@AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            String username = user.getUsername();
            log.info("Accessing editMyPage for user: {}", username);
            try {
                // MemberService를 통해 username으로 MemberResponseDTO 조회
                MemberResponseDTO memberResponseDTO = memberService.getMemberByUsername(username);
                model.addAttribute("member", memberResponseDTO); // 수정 화면에 기존 정보 전달
            } catch (IllegalArgumentException e) {
                log.error("Error finding member for editMyPage: {}", e.getMessage());
                return "redirect:/errorPage";
            }
        } else {
            log.warn("Unauthorized access to editMyPage. Redirecting to login.");
            return "redirect:/login";
        }
        return "modify"; // modify.html 렌더링
    }
}