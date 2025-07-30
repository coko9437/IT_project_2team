package webproject_2team.lunch_matching.controller;

import webproject_2team.lunch_matching.dto.LunchMatchDTO;
import webproject_2team.lunch_matching.service.LunchMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lunchmatches")
@RequiredArgsConstructor
@Log4j2
public class LunchMatchController {

    private final LunchMatchService lunchMatchService;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody LunchMatchDTO lunchMatchDTO) {
        log.info("맛집 등록 요청 (API): " + lunchMatchDTO);
        Long rno = lunchMatchService.register(lunchMatchDTO);

        if (rno == null) {
            return new ResponseEntity<>("이미 저장된 맛집입니다.", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(rno, HttpStatus.CREATED);
    }

    @GetMapping("/{rno}")
    public ResponseEntity<LunchMatchDTO> getOne(@PathVariable Long rno) {
        log.info("특정 맛집 조회 요청 (API): rno = " + rno);
        LunchMatchDTO lunchMatchDTO = lunchMatchService.getOne(rno);
        if (lunchMatchDTO == null) {
            log.warn("조회 요청된 맛집을 찾을 수 없습니다 (API): rno = " + rno);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lunchMatchDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<LunchMatchDTO>> getAllApi() {
        log.info("모든 맛집 조회 요청 (API)");
        List<LunchMatchDTO> lunchMatchDTOS = lunchMatchService.getAll();
        return new ResponseEntity<>(lunchMatchDTOS, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> modify(@Valid @RequestBody LunchMatchDTO lunchMatchDTO) {
        log.info("맛집 수정 요청 (API): " + lunchMatchDTO);
        lunchMatchService.modify(lunchMatchDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{rno}")
    public ResponseEntity<Void> remove(@PathVariable Long rno) {
        log.info("맛집 삭제 요청 (API): rno = " + rno);
        lunchMatchService.remove(rno);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}