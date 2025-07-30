package webproject_2team.lunch_matching.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import webproject_2team.lunch_matching.domain.Inquiry;
import webproject_2team.lunch_matching.dto.InquiryDTO;
import webproject_2team.lunch_matching.repository.InquiryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long register(InquiryDTO inquiryDTO) {
        Inquiry inquiry = modelMapper.map(inquiryDTO, Inquiry.class);
        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        return savedInquiry.getId();
    }

    @Override
    public InquiryDTO read(Long id) {
        Optional<Inquiry> result = inquiryRepository.findById(id);
        Inquiry inquiry = result.orElseThrow(); // 문의가 없으면 예외 발생
        return modelMapper.map(inquiry, InquiryDTO.class);
    }

    @Override
    public List<InquiryDTO> getListOfWriter(String writer) {
        List<Inquiry> inquiries = inquiryRepository.findByWriterOrderByIdDesc(writer);
        return inquiries.stream()
                .map(inquiry -> modelMapper.map(inquiry, InquiryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void addAnswer(Long id, String answer) {
        Optional<Inquiry> result = inquiryRepository.findById(id);
        Inquiry inquiry = result.orElseThrow();

        inquiry.setAnswer(answer);
        inquiry.setAnswered(true);
        inquiry.setAnsweredAt(LocalDateTime.now());

        inquiryRepository.save(inquiry);
    }
}