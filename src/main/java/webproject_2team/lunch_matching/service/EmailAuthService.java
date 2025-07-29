package webproject_2team.lunch_matching.service;

public interface EmailAuthService {
    void sendAuthCode(String email);
    boolean verifyAuthCode(String email, String authCode);
}