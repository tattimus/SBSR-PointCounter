package sbsrcounter.sbsrcounter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Responsible for run related operations and directing
@Controller
public class RunController {

    @Autowired
    private RunRepository rr;

    @Autowired
    private PlayerRepository pr;

    @Autowired
    private GameRepository gr;

    // Fetch and show all runs
    @GetMapping("/runs")
    public String runs(Model model) {
        model.addAttribute("runs", rr.findAll());
        return "runs";
    }

    /* 
    * Delete run specified by path variable id, remove references
    * from player and game, update player points and multiplier
     */
    @GetMapping("/runs/delete/{id}")
    public String deleteRun(@PathVariable Long id) {
        Run deleted = rr.getOne(id);
        Player p = pr.getOne(deleted.getPlayer().getId());
        p.getRuns().remove(deleted);
        p.setPoints(p.getPoints() - deleted.getPoints());
        p.fixMultiplier();
        pr.save(p);
        Game g = gr.getOne(deleted.getGame().getId());
        g.getRuns().remove(deleted);
        gr.save(g);
        rr.delete(deleted);
        return "redirect:/runs";
    }

    /*
    * Add new run. if requested parameters are missing -> redirect. count the points of run
    * with help of games par time, players multiplier and runs completion time.
    * Link new run to player and game, update players multiplier and points.
     */
    @PostMapping("/runs")
    public String addRun(@RequestParam(required = false) Long player, @RequestParam(required = false) Long game,
            @RequestParam(required = false) Integer minutes, @RequestParam(required = false) Integer seconds) {
        try {
            // New run
            Run run = new Run();
            run.setGame(gr.getOne(game));
            run.setPlayer(pr.getOne(player));
            // Completion time to seconds
            int time = 60 * minutes + seconds;
            run.setCompletionTimeInSec(time);
            // Count runs points
            int points = countPointsOfRun(gr.getOne(game), pr.getOne(player), time);
            // Update player
            Player p = pr.getOne(player);
            p.setPoints(p.getPoints() + points);
            p.getRuns().add(run);
            run.setPoints(points);
            // Update and save run and player
            rr.save(run);
            p.fixMultiplier();
            pr.save(p);
            return "redirect:/";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    /* 
    *  Real par time is player.multiplier * game.partime. For every 10% overtime
    *  1 point is lost
    */
    public int countPointsOfRun(Game game, Player player, int completionTime) {
        int parTime = game.getParTimeInSec();
        int points;
        Double multiplier = player.getMultiplier();
        // par time modified according to players multiplier
        int realParTime = (int) (parTime * multiplier);
        // 10% of the real par time
        int tierTime = realParTime / 10;
        // how many 10% is the over time ?
        if ((completionTime - realParTime) > 0) {
            points = 4 - ((completionTime - realParTime) / tierTime);
            // if over 40% overtime, points 0
            if (points < 0) {
                points = 0;
            }
        } else {
            points = 5;
        }
        return points;
    }
}
