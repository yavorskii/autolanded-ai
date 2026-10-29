package ua.pnu.autolanded.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pnu.autolanded.entity.UserPricingConfig;
import java.util.Optional;

@Repository
public interface PricingConfigRepository extends JpaRepository<UserPricingConfig, Long> {
    Optional<UserPricingConfig> findByUserId(Long userId);
}
