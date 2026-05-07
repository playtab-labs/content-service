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
@Table(name = "md_option_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MdOptionGroup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "md_content_id", nullable = false)
    private MdContent mdContent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> name = new HashMap<>();

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    public static MdOptionGroup create(MdContent mdContent, Map<String, String> name, int displayOrder) {
        MdOptionGroup group = new MdOptionGroup();
        group.mdContent = mdContent;
        group.name = new HashMap<>(name);
        group.displayOrder = displayOrder;
        return group;
    }

    public void update(Map<String, String> name, int displayOrder) {
        this.name = new HashMap<>(name);
        this.displayOrder = displayOrder;
    }
}