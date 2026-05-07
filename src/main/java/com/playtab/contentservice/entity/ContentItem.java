package com.playtab.contentservice.entity;

import com.playtab.contentservice.entity.enums.ContentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "content_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 30)
    private ContentType contentType;

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "thumbnail_image_url", length = 500)
    private String thumbnailImageUrl;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible = true;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    public static ContentItem create(ContentType contentType, String thumbnailImageUrl,
                                     boolean isVisible, int displayOrder) {
        ContentItem item = new ContentItem();
        item.contentType = contentType;
        item.thumbnailImageUrl = thumbnailImageUrl;
        item.isVisible = isVisible;
        item.displayOrder = displayOrder;
        return item;
    }

    public void update(String thumbnailImageUrl, boolean isVisible, int displayOrder) {
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.isVisible = isVisible;
        this.displayOrder = displayOrder;
    }
}