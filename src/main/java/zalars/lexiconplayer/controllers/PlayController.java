package zalars.lexiconplayer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import zalars.lexiconplayer.services.Selection;

import java.util.*;

@Controller
@RequestMapping("/lexicon")
public class PlayController {

    private final Selection selection;
    private final List<String> playerList;

    public PlayController(Selection selection) {
        this.selection = selection;
        this.playerList = new ArrayList<>();
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/word-length")
    public String getWordLength() {
        return "word-length";
    }

    @PostMapping("/word-length")
    public String postWordLength(Integer wordLength, Model model) {
        this.playerList.clear();
        String responseStatus = this.selection.buildSelectionBy(wordLength);
        if (responseStatus.startsWith("ERROR")) {
            Map<String, String> errorCauses = Map.of(
                    "1", "сервер словаря недоступен",
                    "2", "неверный интернет-адрес сервера словаря",
                    "3", "какая-то проблема с файлом словаря (RusVocHtml.txt) - " +
                            "он должен находиться рядом с jar-файлом сервера, который теперь остановлен"
            );
            String errorCode = responseStatus.substring(6);
            model.addAttribute("errorCause", errorCauses.get(errorCode));
            return "error";
        }
        return "redirect:/lexicon/player-list";
    }

    @GetMapping("/player-list")
    public String getPlayerList(Model model) {
        model.addAttribute("sourceWord", selection.getWordsBy("SOURCE"));
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
        int guessedWordsNumber = selection.specifyGuessedBy(this.playerList);
        int leftWordsNumber = allDerivedWordsNumber - guessedWordsNumber;
        double rating = 5.00 * guessedWordsNumber / allDerivedWordsNumber;
        model.addAttribute("estimation", selection.estimatePlayerBy(rating));
        model.addAttribute("sourceWord", selection.getWordsBy("SOURCE"));
        model.addAttribute("sourceDefinition", selection.getDefinitionsBy("SOURCE"));
        model.addAttribute("leftDefinitions", selection.getDefinitionsBy("LEFT"));
        model.addAttribute("leftWordsNumber", leftWordsNumber);
        model.addAttribute("guessedWordsNumber", guessedWordsNumber);
        model.addAttribute("guessedWords", selection.getWordsBy("GUESSED"));
        model.addAttribute("leftWords", selection.getWordsBy("LEFT"));
        return "summary";
    }
}
