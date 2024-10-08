package net.mccons.hardware.repository;

import net.mccons.hardware.model.ToolBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolBrandRepository extends JpaRepository<ToolBrand, Long> {
}