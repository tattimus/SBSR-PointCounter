package sbsrcounter.sbsrcounter;

import org.springframework.data.jpa.repository.JpaRepository;

// responsible for interacting with DB table containing Runs
public interface RunRepository extends JpaRepository<Run, Long> {
    
}
