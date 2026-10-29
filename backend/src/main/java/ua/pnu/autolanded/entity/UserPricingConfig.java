package ua.pnu.autolanded.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_pricing_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPricingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Double paintPricePerElement;

    private Double laborRatePerHour;

    private Double airbagRestorePrice;

    private Double geometryBenchHourlyRate;

    private Double brokerAndTerminalFee;

    private Double certificationFee;

    private Double oceanFreightCustomRate;

    private Double usLandTransportCustomRate;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
