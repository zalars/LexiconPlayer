package zalars.lexiconplayer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import zalars.lexiconplayer.services.Selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/lexicon")
public class PlayController {

    private final Selection selection;
    private final List<String> playerList;

    @Autowired
    public PlayController(Selection selection) {
        this.selection = selection;
        this.playerList = new ArrayList<>();
    }

    @GetMapping("/home")
    public String home(Model model) {
        String checkResult = this.selection.checkDictionaryAvailability();
        if (checkResult.startsWith("FAILED")) {
            String errorCause = checkResult.substring(7);
            model.addAttribute("errorCause", errorCause);
            return "error";
        }
        return "home";
    }

    @GetMapping("/word-length")
    public String getWordLength() {
        return "word-length";
    }

    @PostMapping("/word-length")
    public String postWordLength(Integer wordLength) {
        this.playerList.clear();
        selection.buildBy(wordLength);
        return "redirect:/lexicon/player-list";
    }

    @GetMapping("/player-list")
    public String getPlayerList(Model model) {
        model.addAttribute("sourceWord", selection.getSourceWord().toUpperCase());
        model.addAttribute("playerList", this.playerList);
        return "player-list";
    }

    @PostMapping("/player-list")
    public String postPlayerList(String wordRow) {
        // разбивается на массив, т.к. слова могут добавляться через пробел
        this.playerList.addAll(Arrays.asList(wordRow.toLowerCase().split(" +")));
        return "redirect:/lexicon/player-list";
    }

    @GetMapping("/summary")
    public String getSummary(Model model) {
        int allDerivedWordsNumber = selection.getTotalAmount() - 1;  // т.е. за вычетом исходного слова
        int guessedWordsNumber = selection.utilize(this.playerList);
        int leftWordsNumber = allDerivedWordsNumber - guessedWordsNumber;
        double rating = 5.00 * guessedWordsNumber / allDerivedWordsNumber;
        model.addAttribute("estimation", selection.estimatePlayerBy(rating));
        model.addAttribute("sourceWord", selection.getSourceWord().toUpperCase());
        model.addAttribute("sourceDefinition", selection.getSourceDefinition());
        model.addAttribute("leftDefinitions", selection.getLeftDefinitions());
        model.addAttribute("leftWordsNumber", leftWordsNumber);
        model.addAttribute("guessedWordsNumber", guessedWordsNumber);
        model.addAttribute("guessedWords", selection.getWordListBy("GUESSED"));
        model.addAttribute("leftWords", selection.getWordListBy("LEFT"));
        return "summary";
    }
}
