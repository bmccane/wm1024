package net.mccons.hardware.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tool")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tool {
    @Id
    @Column(name = "tool_code", unique = true, nullable = false, updatable = false, insertable = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "type_key", nullable = false)
    private ToolType type;

    @ManyToOne
    @JoinColumn(name = "brand_key", nullable = false)
    private ToolBrand brand;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tool tool)) return false;

        return getCode().equals(tool.getCode());
    }

    @Override
    public int hashCode() {
        return getCode().hashCode();
    }
}
