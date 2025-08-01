package webproject_2team.lunch_matching.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = 1345321375L;

    public static final QReview review = new QReview("review");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<ReviewComment, QReviewComment> comments = this.<ReviewComment, QReviewComment>createList("comments", ReviewComment.class, QReviewComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final StringPath emotion = createString("emotion");

    public final ListPath<UploadResult, QUploadResult> fileList = this.<UploadResult, QUploadResult>createList("fileList", UploadResult.class, QUploadResult.class, PathInits.DIRECT2);

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final ListPath<ReviewLike, QReviewLike> likes = this.<ReviewLike, QReviewLike>createList("likes", ReviewLike.class, QReviewLike.class, PathInits.DIRECT2);

    public final StringPath member_id = createString("member_id");

    public final StringPath menu = createString("menu");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath place = createString("place");

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> review_id = createNumber("review_id", Long.class);

    public QReview(String variable) {
        super(Review.class, forVariable(variable));
    }

    public QReview(Path<? extends Review> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReview(PathMetadata metadata) {
        super(Review.class, metadata);
    }

}

