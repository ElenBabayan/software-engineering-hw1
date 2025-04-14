package com.example.natssubscriber.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NatsMessageRepository extends JpaRepository<NatsMessage, Long> {
}
