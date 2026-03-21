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
}