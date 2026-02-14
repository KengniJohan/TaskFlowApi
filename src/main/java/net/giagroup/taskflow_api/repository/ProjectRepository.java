package net.giagroup.taskflow_api.repository;

import net.giagroup.taskflow_api.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
}
