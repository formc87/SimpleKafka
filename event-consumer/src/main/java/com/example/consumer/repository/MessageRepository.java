package com.example.consumer.repository;

import com.example.consumer.model.MessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 메시지 엔티티를 저장하고 조회하기 위한 Spring Data JPA 리포지토리.
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    /**
     * 수신 시각의 내림차순으로 모든 메시지를 조회한다.
     * @return 최신순 메시지 목록
     */
    List<MessageEntity> findAllByOrderByReceivedAtDesc();
}
