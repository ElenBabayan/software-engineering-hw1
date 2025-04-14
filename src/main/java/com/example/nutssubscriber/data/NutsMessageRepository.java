package com.example.nutssubscriber.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutsMessageRepository extends JpaRepository<NutsMessage, Long> {
}
