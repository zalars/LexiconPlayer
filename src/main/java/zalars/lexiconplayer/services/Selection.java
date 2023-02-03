package zalars.lexiconplayer.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Service
public class Selection {

    // столбцы в массиве value (в records)
    private static final int TAG = 0;
    private static final int DEFINITION = 1;

    private final RequestBureau bureau;
    private final Map<String, String[]> records;  // <слово, [тэг, определение]>
    // тип слова в столбце TAG: "SOURCE" - исходное,
    // "LEFT" - производное (не отгаданное), "GUESSED" - есть в списке игрока

    public Selection(RequestBureau bureau) {
        this.bureau = bureau;
        this.records = new HashMap<>();
    }

    // возвращает "статус" отклика на запрос
    public String buildSelectionBy(int wordLength) {
        this.records.clear();
        String requestResult = this.bureau.sendRequestBy(wordLength);
        if (requestResult.startsWith("ERROR")) {
            return requestResult;
        }
        // entries => "sourceWord@definition@@derivedWord@definition@@derivedWord@definition<...>"
        String[] entries = requestResult.split("@@");
        for (int i = 0; i < entries.length; i++) {
            String[] singleRecord = entries[i].split("@");  // [слово, определение]
            String tag = i > 0 ? "LEFT" : "SOURCE";
            this.records.put(singleRecord[0], new String[]{tag, singleRecord[1]});
        }
        return "OK";
    }

    public int getTotalAmount() {
        return this.records.size();
    }

    public int specifyGuessedBy(List<String> playerList) {
        int guessedWordsNumber = 0;
        for (String word : playerList) {
            if (this.records.containsKey(word)) {
                this.records.get(word)[TAG] = "GUESSED";
                guessedWordsNumber++;
            }
        }
        return guessedWordsNumber;
    }

    public String[] getWordsBy(String tag) {
        List<String> result = new ArrayList<>();
        for (String word : this.records.keySet()) {
            if (tag.equals(this.records.get(word)[TAG])) {
                result.add(word);
            }
        }
        return result.toArray(String[]::new);
    }

    public String[] getDefinitionsBy(String tag) {
        List<String> result = new ArrayList<>();
        for (String word : this.records.keySet()) {
            if (tag.equals(this.records.get(word)[TAG])) {
                result.add(this.records.get(word)[DEFINITION]);
            }
        }
        return result.toArray(String[]::new);
    }

    public String estimatePlayerBy(double rating) {
        String estimation = rating == 5.0 ? "вы великолепны! Мы в восхищении!" :
                rating >= 4.0 ? "замечательный результат!" :
                        rating >= 3.0 ? "очень хорошо!" :
                                rating >= 2.0 ? "неплохо, но вы можете лучше!" :
                                        rating >= 1.0 ? "вы способны на большее!" :
                                                "вы явно схалтурили!";
        return String.format(" %.2f - %s", rating, estimation);
    }
}
