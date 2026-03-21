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
@Table(name = "md_option_values")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MdOptionValue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_group_id", nullable = false)
    private MdOptionGroup optionGroup;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value_name", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> valueName = new HashMap<>();

    @Column(name = "extra_price", nullable = false)
    private int extraPrice = 0;

    @Column(name = "is_sold_out", nullable = false)
    private boolean isSoldOut = false;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;
}