package sbsrcounter.sbsrcounter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Run extends AbstractPersistable<Long> {
    private int completionTimeInSec;
    private int points;
    // Is the run relevant to current or past event
    private boolean current;
    @ManyToOne
    private Player player;
    @ManyToOne
    private Game game;
}
