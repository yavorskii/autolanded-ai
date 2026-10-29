package ua.pnu.autolanded.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pnu.autolanded.entity.CarPart;
import ua.pnu.autolanded.entity.DamageZone;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarPartRepository extends JpaRepository<CarPart, Long> {
    List<CarPart> findByMakeIgnoreCaseAndModelIgnoreCase(String make, String model);
    Optional<CarPart> findFirstByMakeIgnoreCaseAndModelIgnoreCaseAndDamageZone(String make, String model, DamageZone damageZone);
}
