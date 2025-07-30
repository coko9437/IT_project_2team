package webproject_2team.lunch_matching;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import webproject_2team.lunch_matching.domain.Board;
import webproject_2team.lunch_matching.repository.BoardRepository;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
public class BoardDummyDataTest {

    @Autowired
    private BoardRepository boardRepository;

    private static final String[] regions = {"대연동", "광안리", "서면", "전포동"};
    private static final String[] foodCategories = {"한식", "중식", "일식", "분식", "디저트", "종류 안가림"};
    private static final String[] genderLimits = {"남", "여", "성별상관무"};

    private static final String[] sampleContents = {
            "혼밥 그만하고 오늘은 같이 점심 드실 분 찾아요. 분위기 좋은 식당도 알고 있어요!",
            "서면 쪽에서 식사 같이 하실 분 구합니다. 부담 없이 편하게 오세요 😊",
            "점심 혼자 먹기 너무 심심해서 같이 드실 분 찾고 있어요. 식사는 제가 추천할게요!",
            "분위기 좋은 분식집 발견했어요. 맛도 좋고 조용해서 대화도 잘 될 것 같아요!",
            "디저트 좋아하시는 분과 함께 티타임 가지면 좋겠어요. 카페는 광안리 근처입니다 ☕",
            "전포동 신상 맛집 가실 분 있나요? 점심 시간대 맞춰서 조용히 식사하고 싶어요.",
            "간단히 식사하며 얘기 나눌 수 있는 분이면 좋겠어요. 음식 종류는 상관 없어요!"
    };

    @Test
    void insertDummyBoards() {
        Random random = new Random();

        IntStream.rangeClosed(1, 50).forEach(i -> {
            String title = "맛슐랭 테스트 모집글 " + i;
            String randomContent = sampleContents[random.nextInt(sampleContents.length)];
            String fullContent = randomContent + "\n\n※ 이 글은 테스트용 더미 데이터입니다. 실제 모집 글이 아닙니다. [글 번호: " + i + "]";

            Board board = Board.builder()
                    .title(title)
                    .content(fullContent)
                    .writer("user" + i)
                    .region(regions[random.nextInt(regions.length)])
                    .genderLimit(genderLimits[random.nextInt(genderLimits.length)])
                    .foodCategory(foodCategories[random.nextInt(foodCategories.length)])
                    .imagePath(null)
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(30))) // 최근 30일 이내 랜덤
                    .build();

            boardRepository.save(board);
        });
    }
}
