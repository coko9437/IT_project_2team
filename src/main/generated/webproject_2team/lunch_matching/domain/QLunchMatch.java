package webproject_2team.lunch_matching.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLunchMatch is a Querydsl query type for LunchMatch
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLunchMatch extends EntityPathBase<LunchMatch> {

    private static final long serialVersionUID = 1869092738L;

    public static final QLunchMatch lunchMatch = new QLunchMatch("lunchMatch");

    public final StringPath address = createString("address");

    public final StringPath category = createString("category");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath name = createString("name");

    public final StringPath operatingHours = createString("operatingHours");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath priceLevel = createString("priceLevel");

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final NumberPath<Long> rno = createNumber("rno", Long.class);

    public QLunchMatch(String variable) {
        super(LunchMatch.class, forVariable(variable));
    }

    public QLunchMatch(Path<? extends LunchMatch> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLunchMatch(PathMetadata metadata) {
        super(LunchMatch.class, metadata);
    }

}

