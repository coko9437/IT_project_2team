package webproject_2team.lunch_matching.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor // 기본 생성자 추가
public class PageResponseDTO<E> {

    private List<E> dtoList;
    private int totalCount;

    private int page;
    private int size;

    private int start;
    private int end;

    private boolean prev;
    private boolean next;

    // @Builder 어노테이션을 이 생성자에 붙여서 빌더가 이 생성자를 사용하도록 합니다.
    // toBuilder = true는 기존 객체를 기반으로 새로운 빌더를 만들 수 있게 합니다.
    @Builder(toBuilder = true)
    public PageResponseDTO(List<E> dtoList, int totalCount, int page, int size) {
        this.dtoList = dtoList;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;

        // 페이지네이션 계산
        int lastPage = (int)(Math.ceil(totalCount / (double)size));
        if (totalCount == 0) {
            lastPage = 1;
        }
        this.end = (int)(Math.ceil(this.page / 10.0)) * 10;
        this.start = this.end - 9;

        if(this.start <= 0) {
            this.start = 1;
        }
        if(lastPage < this.end) {
            this.end = lastPage;
        }

        this.prev = this.start > 1;
        this.next = this.end < lastPage;
    }
}