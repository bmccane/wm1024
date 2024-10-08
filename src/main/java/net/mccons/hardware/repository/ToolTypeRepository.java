package net.mccons.hardware.repository;

import net.mccons.hardware.model.ToolType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolTypeRepository extends JpaRepository<ToolType, Long> {
}