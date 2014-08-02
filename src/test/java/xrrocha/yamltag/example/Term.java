package xrrocha.yamltag.example;

import java.util.Map;

public class Term {
    private String englishWord;
    Map<Language, String> translations;
    
    public String getEnglishWord() {
        return englishWord;
    }
    public void setEnglishWord(String englishWord) {
        this.englishWord = englishWord;
    }
    public Map<Language, String> getTranslations() {
        return translations;
    }
    public void setTranslations(Map<Language, String> translations) {
        this.translations = translations;
    }
}
