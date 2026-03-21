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
@Table(name = "food_truck_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodTruckContent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_item_id", nullable = false, unique = true)
    private ContentItem contentItem;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> name = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "short_description", columnDefinition = "jsonb")
    private Map<String, String> shortDescription = new HashMap<>();
}