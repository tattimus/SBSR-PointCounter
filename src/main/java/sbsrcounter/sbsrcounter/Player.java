package sbsrcounter.sbsrcounter;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Player extends AbstractPersistable<Long> {

    private String name;
    // sum of players points
    private int points;
    // multiplier affecting next runs time limit
    private double multiplier;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Run> runs = new ArrayList<>();

    // count multiplier from completed runs, used after deleting/adding runs
    public void fixMultiplier() {
        Double d = 1.0;
        for (Run run : runs) {
            if (run.isCurrent() == true) {
                d = updateMultiplier(d, run.getPoints());
            }
        }
        this.multiplier = d;
    }

    // increase/decrease multiplier, action depends on runs points and current multiplier
    private Double updateMultiplier(Double mult, int points) {
        Double updated = mult;
        if (points < 5 && updated < 1) {
            updated = 1.0;
        } else if (points == 5 && updated <= 1) {
            updated -= 0.1;
        } else if (points < 5 && updated >= 1) {
            updated += 0.1;
        } else {
            updated = 1.0;
        }
        return updated;
    }
    
    public void updateCurrentPoints() {
        int sum = 0;
        for (Run run : runs) {
            if (run.isCurrent()== true) {
                sum += run.getPoints();
            }
        }
        points = sum;
    }
}
