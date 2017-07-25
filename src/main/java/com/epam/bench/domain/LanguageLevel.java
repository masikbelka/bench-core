package com.epam.bench.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A LanguageLevel.
 */
@Entity
@Table(name = "language_level")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "languagelevel")
public class LanguageLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "speaking")
    private String speaking;

    @Column(name = "writing")
    private String writing;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public LanguageLevel language(String language) {
        this.language = language;
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSpeaking() {
        return speaking;
    }

    public LanguageLevel speaking(String speaking) {
        this.speaking = speaking;
        return this;
    }

    public void setSpeaking(String speaking) {
        this.speaking = speaking;
    }

    public String getWriting() {
        return writing;
    }

    public LanguageLevel writing(String writing) {
        this.writing = writing;
        return this;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LanguageLevel languageLevel = (LanguageLevel) o;
        if (languageLevel.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), languageLevel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LanguageLevel{" +
            "id=" + getId() +
            ", language='" + getLanguage() + "'" +
            ", speaking='" + getSpeaking() + "'" +
            ", writing='" + getWriting() + "'" +
            "}";
    }
}
