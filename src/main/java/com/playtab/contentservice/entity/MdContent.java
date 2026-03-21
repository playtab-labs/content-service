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
@Table(name = "md_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MdContent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_item_id", nullable = false, unique = true)
    private ContentItem contentItem;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> name = new HashMap<>();

    @Column(name = "price", nullable = false)
    private Integer price;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product_description", columnDefinition = "jsonb")
    private Map<String, String> productDescription = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detail_description", columnDefinition = "jsonb")
    private Map<String, String> detailDescription = new HashMap<>();

    @Column(name = "detail_image_url", length = 500)
    private String detailImageUrl;

    @Column(name = "is_sold_out", nullable = false)
    private boolean isSoldOut = false;
}