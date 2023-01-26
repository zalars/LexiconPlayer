package zalars.lexiconplayer.services;

import org.springframework.stereotype.Service;
import zalars.lexiconplayer.models.Selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Gameplay {

    private final RequestBureau bureau;
    private final Selection selection;
    private final List<String> lexicon;   // список слов игрока
    private final List<Double> ratings;


    public Gameplay(RequestBureau bureau, Selection selection) {
        this.bureau = bureau;
        this.selection = selection;
        this.lexicon = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }

    public Selection readSelection() {
        return this.selection;
    }

    public List<String> readLexicon() {
        return this.lexicon;
    }

    public void generateSelection(int wordLength) {
        String[] fullSelection = this.bureau.requestSelection(wordLength).split("@@@");
        String[] sourceWithDefinitions = fullSelection[0].split("@");
        this.selection.setSourceWord(sourceWithDefinitions[0]);
        this.selection.setSourceDefinitions(sourceWithDefinitions[1]);

        this.lexicon.clear();
        Map<String, String> derivedWords = this.selection.getDerived();
        derivedWords.clear();

        String[] derivedRecords = fullSelection[1].split("@@");
        for (String record : derivedRecords) {
            String[] wordAndDefinitions = record.split("@");
            derivedWords.put(wordAndDefinitions[0], wordAndDefinitions[1]);
        }
    }

    public String estimatePlayer() {
        double currentRating = 5.00 * this.lexicon.size() / (this.lexicon.size() + this.selection.getDerived().size());
        this.ratings.add(currentRating);
        double cumulativeRating = this.ratings.stream().reduce(Double::sum).orElse(0.0) / this.ratings.size();
        String estimation = cumulativeRating == 5.0 ? "вы великолепны! Мы в восхищении!" :
                cumulativeRating >= 4.0 ? "замечательный результат!" :
                        cumulativeRating >= 3.0 ? "очень хорошо!" :
                                cumulativeRating >= 2.0 ? "неплохо, но вы можете лучше!" :
                                        cumulativeRating >= 1.0 ? "вы способны на большее!" :
                                                "вы явно схалтурили!";
        return String.format(" %.2f - %s", cumulativeRating, estimation);
    }

    public void reviseLists() {
        List<String> derivedWords = new ArrayList<>(this.selection.getDerived().keySet());
        lexicon.retainAll(derivedWords);   // в списке игрока остаются только подходящие слова
        for (String word : lexicon) {
            if (derivedWords.contains(word)) { // из эталонного списка удаляются слова из списка игрока
                this.selection.getDerived().remove(word);
            }
        }
    }

}
