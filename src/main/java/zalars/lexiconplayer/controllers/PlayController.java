package zalars.lexiconplayer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import zalars.lexiconplayer.models.Selection;
import zalars.lexiconplayer.services.Gameplay;

import java.util.Arrays;

@Controller
@RequestMapping("/lexicon")
public class PlayController {

    private final Gameplay gameplay;

    @Autowired
    public PlayController(Gameplay gameplay) {
        this.gameplay = gameplay;
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
    public String postWordLength(Integer wordLength) {
        gameplay.generateSelection(wordLength);
        return "redirect:/lexicon/player-list";
    }

    @GetMapping("/player-list")
    public String getPlayerList(Model model) {
        model.addAttribute("sourceWord", gameplay.readSelection().getSourceWord().toUpperCase());
        model.addAttribute("lexicon", gameplay.readLexicon());
        return "player-list";
    }

    @PostMapping("/player-list")
    public String postPlayerList(String anotherWord) {
        // разбивается на массив, т.к. слова могут добавляться через пробел
        Arrays.stream(anotherWord.toLowerCase().split(" +")).forEach(gameplay.readLexicon()::add);
        return "redirect:/lexicon/player-list";
    }

    @GetMapping("/summary")
    public String getSummary(Model model) {
        gameplay.reviseLists();
        Selection selection = gameplay.readSelection();
        String[] derivedWords = selection.getDerived().keySet().toArray(String[]::new);
        model.addAttribute("sourceWord", selection.getSourceWord().toUpperCase());
        model.addAttribute("estimation", gameplay.estimatePlayer());
        model.addAttribute("lexiconSize", gameplay.readLexicon().size());
        model.addAttribute("derivedWordsNumber", derivedWords.length);
        model.addAttribute("lexicon", gameplay.readLexicon());
        model.addAttribute("derivedWords", derivedWords);
        model.addAttribute("sourceDefinitions", selection.getSourceDefinitions());
        model.addAttribute("derivedDefinitions", selection.getDerived().values().toArray(String[]::new));
        return "summary";
    }
    
}
