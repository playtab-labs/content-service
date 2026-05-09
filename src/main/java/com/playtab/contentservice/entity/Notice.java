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

    @Column(name = "image_url", length = 2000)
    private String imageUrl;

    public static Notice create(Map<String, String> title, Map<String, String> content,
                                LocalDateTime postedAt, boolean isPinned, boolean isVisible,
                                String imageUrl) {
        Notice notice = new Notice();
        notice.title = title != null ? title : new HashMap<>();
        notice.content = content != null ? content : new HashMap<>();
        notice.postedAt = postedAt;
        notice.isPinned = isPinned;
        notice.isVisible = isVisible;
        notice.imageUrl = imageUrl;
        return notice;
    }

    public void update(Map<String, String> title, Map<String, String> content,
                       LocalDateTime postedAt, boolean isPinned, boolean isVisible,
                       String imageUrl) {
        this.title = title != null ? title : new HashMap<>();
        this.content = content != null ? content : new HashMap<>();
        this.postedAt = postedAt;
        this.isPinned = isPinned;
        this.isVisible = isVisible;
        this.imageUrl = imageUrl;
    }
}