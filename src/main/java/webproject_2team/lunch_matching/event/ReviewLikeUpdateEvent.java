package webproject_2team.lunch_matching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReviewLikeUpdateEvent extends ApplicationEvent {
    private final Long reviewId;
    private final int likeCount;

    public ReviewLikeUpdateEvent(Object source, Long reviewId, int likeCount) {
        super(source);
        this.reviewId = reviewId;
        this.likeCount = likeCount;
    }
}
