package org.example.popspace.mapper.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.reservation.ReservationPopupCacheDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class ReservationRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ReservationRedisRepository(@Qualifier("reservationRedisTemplate") StringRedisTemplate redisTemplate,
                                      ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;

        // 연결 확인
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        if (factory != null) {
            try (RedisConnection connection = factory.getConnection()) {
                String pong = connection.ping();
                log.info("[O] Redis ping 응답: {}", pong);
            } catch (Exception e) {
                log.error("[X] Redis 연결 실패", e);
            }
        }
    }

    // 팝업 정보 가져오기
    public ReservationPopupCacheDTO getPopupInfo(String key) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;

        try {
            return objectMapper.readValue(json, ReservationPopupCacheDTO.class);
        } catch (JsonProcessingException e) {
            log.error("[redis]: 팝업 정보 역직렬화 실패. key: {}, value: {}", key, json, e);
            return null;
        }
    }

    // 팝업 정보 저장하기
    public void setPopupInfo(String key, ReservationPopupCacheDTO dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            log.error("[redis]: 팝업 정보 직렬화 실패. key: {}, object: {}", key, dto, e);
        }
    }


    // 예약 명단에 멤버 존재 여부 확인 / 없으면 추가
    public boolean tryAddMemberToSet(String key, long memberId) {
        String script = """
        if redis.call("SISMEMBER", KEYS[1], ARGV[1]) == 1 then
            return 1
        else
            redis.call("SADD", KEYS[1], ARGV[1])
            return 0
        end
        """;

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                String.valueOf(memberId)
        );

        return result != null && result == 1L;
    }

    // 예약 명단에서 멤버 제거
    public void removeMemberFromSet(String key, long memberId) {
        redisTemplate.opsForSet().remove(key, String.valueOf(memberId));
    }

    // 해당 시간대 예약수 < max: 증가
    public boolean tryIncrementCount(String key, int max) {
        String script = """
        local current = redis.call("INCR", KEYS[1])
        if tonumber(current) > tonumber(ARGV[1]) then
            redis.call("DECR", KEYS[1])
            return 0
        else
            return 1
        end
        """;

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                String.valueOf(max)
        );

        return result != null && result == 1L;
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // count key 없을 때만 값 저장 (오직 한 번만 set)
    public void setCountIfAbsent(String key, int count) {
        String script = """
        if redis.call("EXISTS", KEYS[1]) == 0 then
            redis.call("SET", KEYS[1], ARGV[1])
        end
        """;

        redisTemplate.execute(
                new DefaultRedisScript<>(script, Void.class),
                Collections.singletonList(key),
                String.valueOf(count)
        );
    }

    // 사전예약/웨이팅: count 감소 (예약 실패/취소 등)
    public Long decrementCount(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    // 사전예약/웨이팅: 현재 예약 수 조회 (시간대별)
    public String getCount(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * DB 싱크 작업
     */
    // 기존 값 삭제
    public void clearSet(String key) {
        redisTemplate.delete(key);
    }

    public void setCount(String key, int count) {
        redisTemplate.opsForValue().set(key, String.valueOf(count));
    }

    public void addMembersToSet(String key, List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) return;

        String[] members = memberIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);

        redisTemplate.opsForSet().add(key, members);
    }


}

