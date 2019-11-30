package sbsrcounter.sbsrcounter;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// responsible for game related operations and directing
@Controller
public class GameController {

    @Autowired
    private GameRepository gr;

    // create new game from given parameters
    @PostMapping("/games")
    public String addGame(@RequestParam String name, @RequestParam int parTime, @RequestParam String description) {
        Game newGame = new Game();
        newGame.setName(name);
        newGame.setParTimeInSec(parTime);
        newGame.setDescription(description);
        gr.save(newGame);
        return "redirect:/games";
    }

    // fetch all games for listing
    @GetMapping("/games")
    public String games(Model model) {
        model.addAttribute("games", gr.findAll());
        return "games";
    }

    // delete game with the id specified in path variable id
    @GetMapping("/games/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        Game g = gr.getOne(id);
        gr.delete(g);
        return "redirect:/games";
    }
    
    // show game specific page, contains basic info of the game and form for calculating point tiers
    @GetMapping("/games/{id}")
    public String showGame(Model model, @PathVariable Long id, @RequestParam(required = false) Double multiplier) {
        double mult = 1.0;
        if (multiplier != null) {
            mult = multiplier;
        }
        Game g = gr.getOne(id);
        model.addAttribute("game", g);
        model.addAttribute("timeTiers", g.countPointTiers(mult));
        model.addAttribute("defaultMult", mult);
        return "game";
    }
}
