package net.giagroup.taskflow_api.repository;

import net.giagroup.taskflow_api.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectId(UUID projectId);
}
