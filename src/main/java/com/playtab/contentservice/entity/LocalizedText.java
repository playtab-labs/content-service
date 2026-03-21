package com.playtab.contentservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Embeddable
public class LocalizedText {

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value", columnDefinition = "jsonb")
    private Map<String, String> texts = new HashMap<>();

    public LocalizedText(Map<String, String> texts) {
        this.texts = texts;
    }
}