package sbsrcounter.sbsrcounter;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SBSRcounterController {

    @Autowired
    private GameRepository gr;
    @Autowired
    private RunRepository rr;
    @Autowired
    private PlayerRepository pr;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("players", pr.findAll());
        model.addAttribute("games", gr.findAll());
        return "index";
    }

    @GetMapping("/player/{id}")
    public String showPlayer(Model model, @PathVariable Long id) {
        model.addAttribute("player", pr.getOne(id));
        model.addAttribute("runs", pr.getOne(id).getRuns());
        return "player";
    }

    public int countPointsOfRun(Game game, Player player, int completionTime) {
        int parTime = game.getParTimeInSec();
        int points;
        Double multiplier = player.getMultiplier();
        int realParTime = (int) (parTime * multiplier);
        int tierTime = realParTime / 10;
        if ((completionTime - realParTime) > 0) {
            points = 4 - ((completionTime - realParTime) / tierTime);
        } else {
            points = 5;
        }
        return points;
    }

    @PostMapping("/player")
    public String addPlayer(@RequestParam String name) {
        Player newPlayer = new Player();
        newPlayer.setName(name);
        newPlayer.setPoints(0);
        newPlayer.setMultiplier(1);
        pr.save(newPlayer);
        return "redirect:/";
    }

    @PostMapping("/games")
    public String addGame(@RequestParam String name, @RequestParam int parTime, @RequestParam String description) {
        Game newGame = new Game();
        newGame.setName(name);
        newGame.setParTimeInSec(parTime);
        newGame.setDescription(description);
        gr.save(newGame);
        return "redirect:/games";
    }

    @GetMapping("/games")
    public String games(Model model) {
        model.addAttribute("games", gr.findAll());
        return "games";
    }

    @PostMapping("/runs")
    public String addRun(@RequestParam Long player, @RequestParam Long game, @RequestParam int minutes, @RequestParam int seconds) {
        if (player == null || game == null || minutes < 1 || seconds < 0) {
            return "redirect:/";
        }
        Run run = new Run();
        run.setGame(gr.getOne(game));
        run.setPlayer(pr.getOne(player));
        int time = 60 * minutes + seconds;
        run.setCompletionTimeInSec(time);
        int points = countPointsOfRun(gr.getOne(game), pr.getOne(player), time);
        Player p = pr.getOne(player);
        p.setPoints(p.getPoints() + points);
        p = updatePlayerMultiplier(p, points);
        p.getRuns().add(run);
        run.setPoints(points);
        rr.save(run);
        pr.save(p);
        return "redirect:/";
    }
    
    @GetMapping("/runs/delete/{id}")
    public String deleteRun(@PathVariable Long id) {
        Run deleted = rr.getOne(id);
        Player p = pr.getOne(deleted.getPlayer().getId());
        p.getRuns().remove(deleted);
        p.setPoints(p.getPoints() - deleted.getPoints());
        pr.save(p);
        Game g = gr.getOne(deleted.getGame().getId());
        g.getRuns().remove(deleted);
        gr.save(g);
        rr.delete(deleted);
        return "redirect:/";
    }
    
    @GetMapping("/games/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        Game g = gr.getOne(id);
        List<Run> runs = g.getRuns();
        for (Run run : runs) {
            Player p = run.getPlayer();
            p.getRuns().remove(run);
            pr.save(p);
            rr.delete(run);
        }
        gr.delete(g);
        // remove runs associated, remove runs from players that have them.
        // atm runs have to be deleted before game, if not app will crash
        return "redirect:/games";
    }
    
    @GetMapping("/player/delete")
    public String deletePlayer(@RequestParam Long id) {
        Player p = pr.getOne(id);
        List<Run> runs = p.getRuns();
        for (Run run : runs) {
            Game g = run.getGame();
            g.getRuns().remove(run);
            gr.save(g);
            rr.delete(run);
            p.getRuns().remove(run);
        }
        pr.delete(p);
        // remove runs associated, remove runs from games that have them.
        // atm runs have to be deleted before player, if not app will crash
        return "redirect:/";
    }

    @GetMapping("/runs")
    public String runs(Model model) {
        model.addAttribute("runs", rr.findAll());
        return "runs";
    }
    
    @GetMapping("/addPlayer")
    public String addPlayer() {
        return "addPlayer";
    }
    
    @GetMapping("/challenge")
    public String challenge(Model model) {
        model.addAttribute("players", pr.findAll());
        return "challenge";
    }
    
    @PostMapping("/challenge")
    public String challengeResult(@RequestParam long win, @RequestParam long los) {
        Player winner = pr.getOne(win);
        Player loser = pr.getOne(los);
        challengePointSwap(winner, loser);
        return "redirect:/";
    }
    
    public Player updatePlayerMultiplier(Player p, int points) {
        Player updated = p;
        if (points < 5 && updated.getMultiplier() < 1) {
            updated.setMultiplier(1);
        } else if (points == 5 && updated.getMultiplier() <= 1) {
            updated.setMultiplier(updated.getMultiplier() - 0.1);
        } else if (points < 5 && updated.getMultiplier() >= 1) {
            updated.setMultiplier(updated.getMultiplier() + 0.1);
        } else {
            updated.setMultiplier(1);
        }
        return updated;
    }
    
    public void challengePointSwap(Player winner, Player loser) {
        int price = loser.getPoints() / 2;
        loser.setPoints(loser.getPoints()/2);
        winner.setPoints(winner.getPoints() + price);
        pr.save(winner);
        pr.save(loser);
    }

}
