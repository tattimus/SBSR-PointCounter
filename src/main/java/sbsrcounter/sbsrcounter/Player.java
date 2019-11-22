package sbsrcounter.sbsrcounter;

import java.util.ArrayList;
import java.util.List;
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
    private int points;
    private double multiplier;
    @OneToMany
    private List<Run> runs = new ArrayList<>();
    
}
