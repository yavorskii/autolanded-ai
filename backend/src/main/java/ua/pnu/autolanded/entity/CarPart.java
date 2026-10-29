package ua.pnu.autolanded.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "car_parts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    private Integer yearFrom;

    private Integer yearTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageZone damageZone;

    @Column(nullable = false)
    private String partNameUa;

    private String partNameEn;

    @Column(nullable = false)
    private String oemPartNumber;

    private Integer diagramPosition;

    private String diagramImageUrl;

    private Double taiwanNewPriceUsd;

    private Double usedOriginalInColorPriceUsd;

    private Double usedOriginalToPaintPriceUsd;

    @Builder.Default
    private Double laborHoursToReplace = 2.0;

    @Builder.Default
    private boolean requiresPaint = true;
}
