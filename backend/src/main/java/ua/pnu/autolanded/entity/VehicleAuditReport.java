package ua.pnu.autolanded.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_audit_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleAuditReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String vin;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    private Integer year;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private Double auctionPriceUsd;

    private Double totalCustomsAndClearanceUsd;

    private Double optimisticRepairCostUsd;

    private Double realisticRepairCostUsd;

    private Double pessimisticRepairCostUsd;

    private Double totalLandedTurnkeyUsd;

    private Double ukraineMarketBottomPriceUsd;

    private Double ukraineMarketMedianPriceUsd;

    private Double ukraineMarketTopPriceUsd;

    private Double netProfitUsd;

    private Double roiPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditVerdict verdict;

    @Column(columnDefinition = "TEXT")
    private String summaryTextUa;

    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
