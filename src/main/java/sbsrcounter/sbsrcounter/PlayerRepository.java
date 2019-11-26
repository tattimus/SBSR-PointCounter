package sbsrcounter.sbsrcounter;

import org.springframework.data.jpa.repository.JpaRepository;

// responsible for interacting with DB table containing Players
public interface PlayerRepository extends JpaRepository<Player, Long> {
    
}
