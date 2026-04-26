package com.playtab.contentservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "notices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "title", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> title = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> content = new HashMap<>();

    @Column(name = "posted_at", nullable = false)
    private LocalDateTime postedAt;

    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned = false;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible = true;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}