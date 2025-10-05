package com.example.consumer.service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Kafka 클러스터 메타데이터를 조회해 파티션별 리더 브로커 정보를 반환하는 서비스.
 */
@Service
public class ClusterMetadataService {

    private static final Logger log = LoggerFactory.getLogger(ClusterMetadataService.class);

    /** Kafka 메타데이터 조회를 위한 AdminClient */
    private final AdminClient adminClient;

    public ClusterMetadataService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    /**
     * 특정 토픽/파티션의 리더 브로커 정보를 조회한다.
     * @param topic   토픽 이름
     * @param partition 파티션 번호
     * @return 리더 브로커 정보 (조회 실패 시 빈 Optional)
     */
    public Optional<BrokerInfo> findLeader(String topic, int partition) {
        try {
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singleton(topic));
            TopicDescription description = result.values().get(topic).get();
            return description.partitions().stream()
                    .filter(info -> info.partition() == partition)
                    .map(TopicPartitionInfo::leader)
                    .filter(node -> node != null)
                    .findFirst()
                    .map(node -> new BrokerInfo(node.id(), node.host(), node.port()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("토픽 {} 파티션 {} 리더 조회 중 인터럽트가 발생했습니다.", topic, partition, e);
        } catch (ExecutionException e) {
            log.warn("토픽 {} 파티션 {} 리더 정보를 가져오지 못했습니다: {}", topic, partition, e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * 브로커 id/호스트/포트 정보를 담는 단순 DTO.
     * @param id    브로커 id
     * @param host  브로커 호스트명
     * @param port  브로커 포트
     */
    public record BrokerInfo(int id, String host, int port) {
    }
}
