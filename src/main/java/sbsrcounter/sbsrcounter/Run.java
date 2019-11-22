/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    @ManyToOne
    private Player player;
    @ManyToOne
    private Game game;
}
