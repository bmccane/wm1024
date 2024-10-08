package net.mccons.hardware.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tool_type")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ToolType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "type_key", nullable = false, updatable = false, insertable = false)
    private Long id;

    @Column(name = "type_name", unique = true, nullable = false)
    private String name;

    @Column(name = "daily_charge")
    private Double charge;

    @Column(name = "weekday")
    private boolean weekday;

    @Column(name = "weekend")
    private boolean weekend;

    @Column(name = "holiday")
    private boolean holiday;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToolType toolType)) return false;

        return getId().equals(toolType.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
