package webproject_2team.lunch_matching.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUploadResult is a Querydsl query type for UploadResult
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUploadResult extends BeanPath<UploadResult> {

    private static final long serialVersionUID = 1816948357L;

    public static final QUploadResult uploadResult = new QUploadResult("uploadResult");

    public final StringPath fileName = createString("fileName");

    public final BooleanPath image = createBoolean("image");

    public final BooleanPath isImage = createBoolean("isImage");

    public final StringPath uuid = createString("uuid");

    public QUploadResult(String variable) {
        super(UploadResult.class, forVariable(variable));
    }

    public QUploadResult(Path<? extends UploadResult> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUploadResult(PathMetadata metadata) {
        super(UploadResult.class, metadata);
    }

}

