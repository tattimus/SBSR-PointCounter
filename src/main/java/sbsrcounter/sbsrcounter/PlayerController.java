package sbsrcounter.sbsrcounter;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// responsible for player related operations and directing
@Controller
public class PlayerController {

    @Autowired
    private PlayerRepository pr;

    // fetch players information and runs for persons personal page
    @GetMapping("/player/{id}")
    public String showPlayer(Model model, @PathVariable Long id) {
        model.addAttribute("player", pr.getOne(id));
        model.addAttribute("runs", pr.getOne(id).getRuns());
        return "player";
    }

    // add new player with given name and set the points and multiplier to default values
    @PostMapping("/player")
    public String addPlayer(@RequestParam String name) {
        Player newPlayer = new Player();
        newPlayer.setName(name);
        newPlayer.setPoints(0);
        newPlayer.setMultiplier(1);
        pr.save(newPlayer);
        return "redirect:/";
    }

    // delete specified player from DB
    @GetMapping("/player/delete")
    public String deletePlayer(@RequestParam Long id) {
        Player p = pr.getOne(id);
        pr.delete(p);
        return "redirect:/";
    }

    // direct to page with form for adding player
    @GetMapping("/addPlayer")
    public String addPlayer() {
        return "addPlayer";
    }

    // fetch players for challenge form
    @GetMapping("/challenge")
    public String challenge(Model model) {
        model.addAttribute("players", pr.findAll());
        return "challenge";
    }

    // winner gets half of the losers points
    @PostMapping("/challenge")
    public String challengeResult(@RequestParam long win, @RequestParam long los) {
        Player winner = pr.getOne(win);
        Player loser = pr.getOne(los);
        challengePointSwap(winner, loser);
        return "redirect:/";
    }

    // reset all players to default values, not name
    @Transactional
    @GetMapping("/players/reset")
    public String resetPlayerPointsAndMultiplier() {
        List<Player> players = pr.findAll();
        for (Player p : players) {
            resetPlayer(p);
        }
        return "redirect:/";
    }

    // move half of the losers points to the winner and save players to DB
    public void challengePointSwap(Player winner, Player loser) {
        int price = loser.getPoints() / 2;
        loser.setPoints(loser.getPoints() / 2);
        winner.setPoints(winner.getPoints() + price);
        pr.save(winner);
        pr.save(loser);
    }

    // reset points and multiplier to default and save
    @Transactional
    public void resetPlayer(Player p) {
        p.setMultiplier(1);
        p.setPoints(0);
        pr.save(p);
    }
}
