package sbsrcounter.sbsrcounter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// responsible for root request
@Controller
public class DefaultController {

    @Autowired
    private GameRepository gr;

    @Autowired
    private PlayerRepository pr;

    // main page, players and games fetched for the new run form and scoreboard
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("players", pr.findAll());
        model.addAttribute("games", gr.findAll());
        return "index";
    }
}
