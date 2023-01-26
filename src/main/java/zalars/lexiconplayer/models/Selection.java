package zalars.lexiconplayer.models;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Selection {

    private String sourceWord;
    private String sourceDefinitions;
    private final Map<String, String> derived;   // полный список слов (из исх. слова) и их определений

    public Selection() {
        this.sourceWord = "";
        this.sourceDefinitions = "";
        this.derived = new HashMap<>();
    }

    public String getSourceWord() {
        return sourceWord;
    }

    public void setSourceWord(String sourceWord) {
        this.sourceWord = sourceWord;
    }

    public String getSourceDefinitions() {
        return sourceDefinitions;
    }

    public void setSourceDefinitions(String sourceDefinitions) {
        this.sourceDefinitions = sourceDefinitions;
    }

    public Map<String, String> getDerived() {
        return derived;
    }
}
