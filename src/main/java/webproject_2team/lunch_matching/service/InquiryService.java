package webproject_2team.lunch_matching.service;

import webproject_2team.lunch_matching.dto.InquiryDTO;

import java.util.List;

public interface InquiryService {

    // 문의 등록
    Long register(InquiryDTO inquiryDTO);

    // 문의 하나 조회
    InquiryDTO read(Long id);

    // 특정 사용자의 모든 문의 조회
    List<InquiryDTO> getListOfWriter(String writer);

    // (관리자용) 문의 답변
    void addAnswer(Long id, String answer);
}