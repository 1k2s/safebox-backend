package com.senai.safebox.wapAPI.repository;

import com.senai.safebox.wapAPI.domain.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}
