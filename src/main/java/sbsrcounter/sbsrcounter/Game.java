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

public class Game extends AbstractPersistable<Long> {
    private String name;
    //Time limit without player multiplier for getting 5 points
    private int parTimeInSec;
    private String description;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Run> runs = new ArrayList<>();
    
    // return list of times (in seconds) for all point tiers
    public List<Integer> countPointTiers(double multiplier) {
        List<Integer> timesInSec = new ArrayList<>();
        int realParTime = (int) (multiplier * parTimeInSec);
        timesInSec.add(realParTime);
        int pointTierTime = realParTime / 10;
        for (int i = 0; i < 4; i++) {
            realParTime = realParTime + pointTierTime;
            timesInSec.add(realParTime);
        }
        return timesInSec;
    }
}
