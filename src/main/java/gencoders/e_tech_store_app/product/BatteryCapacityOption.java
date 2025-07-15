package gencoders.e_tech_store_app.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class BatteryCapacityOption {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false, length = 20) private String value; // 4323mAh
}

