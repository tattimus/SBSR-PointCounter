package sbsrcounter.sbsrcounter;

import org.springframework.data.jpa.repository.JpaRepository;

// responsible for interacting with DB table containing Games
public interface GameRepository extends JpaRepository<Game, Long> {
    
}
