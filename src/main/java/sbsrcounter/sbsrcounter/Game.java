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
}
