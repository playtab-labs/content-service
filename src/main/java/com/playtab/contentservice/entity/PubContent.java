package com.playtab.contentservice.entity;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "pub_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PubContent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_item_id", nullable = false, unique = true)
    private ContentItem contentItem;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "college_name", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> collegeName = new HashMap<>();

    @Column(name = "is_name_confirmed", nullable = false)
    private boolean isNameConfirmed = false;

    public static PubContent create(ContentItem contentItem,
                                    Map<String, String> collegeName,
                                    boolean isNameConfirmed) {
        PubContent pub = new PubContent();
        pub.contentItem = contentItem;
        pub.collegeName = new HashMap<>(collegeName);
        pub.isNameConfirmed = isNameConfirmed;
        return pub;
    }

    public void update(Map<String, String> collegeName, boolean isNameConfirmed) {
        this.collegeName = new HashMap<>(collegeName);
        this.isNameConfirmed = isNameConfirmed;
    }
}