package com.epam.bench.web.rest.dto;

public class LanguageLevelDto {

    private String language;
    private String speaking;
    private String writing;

    public LanguageLevelDto() {
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSpeaking() {
        return speaking;
    }

    public void setSpeaking(String speaking) {
        this.speaking = speaking;
    }

    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }
}
