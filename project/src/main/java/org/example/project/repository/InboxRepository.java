package org.example.project.repository;

import org.example.project.entity.internal.InboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<InboxMessage, Long> {
}
