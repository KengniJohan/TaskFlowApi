package net.giagroup.taskflow_api.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Builder.Default
    private String status = "TODO";

    @Builder.Default
    private String priority = "MEDIUM";

    private Instant dueDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "task_assignees",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private Set<User> assignees = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "task_tags",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}
