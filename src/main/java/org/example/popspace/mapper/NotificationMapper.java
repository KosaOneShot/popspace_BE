package org.example.popspace.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.example.popspace.dto.notification.NotificationResponseDto;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NotificationMapper {
    @Insert("""
        INSERT INTO NOTIFICATION (
            NOTIFY_ID, POPUP_ID, TITLE, CONTENT, IMAGE_URL, NOTIFICATION_STATE, CREATED_AT
        ) VALUES (
            #{notifyId}, #{popupId}, #{title}, #{content}, #{imageUrl}, #{notificationState}, SYSDATE
        )
    """)
    @SelectKey(statement = "SELECT SEQ_NOTIFY_ID.NEXTVAL FROM DUAL", keyProperty = "notifyId", before = true, resultType = Long.class)
    void insertNotification(NotificationResponseDto notification);

    @Select("""
        SELECT NOTIFY_ID,
               N.POPUP_ID,
               TITLE,
               CONTENT,
               IMAGE_URL,
               N.CREATED_AT,
               NOTIFICATION_STATE
        FROM NOTIFICATION N
                 JOIN RESERVATION R ON N.popup_id = R.popup_id
                 JOIN MEMBER M ON R.member_id = M.member_id
        WHERE R.MEMBER_ID = #{memberId}
                  AND R.reservation_state = 'RESERVED'
                  AND N.notification_state = 'ACTIVE'
    """)
    List<NotificationResponseDto> selectNotificationsByMemberId(long memberId);

    @Select("""
        SELECT DISTINCT MEMBER_ID
        FROM RESERVATION R
        WHERE POPUP_ID = #{popupId}
            AND RESERVATION_STATE = 'RESERVED'
    """)
    List<Long> selectReservedMemberIds(long popupId);

    @Select("""
        SELECT POPUP_ID
        FROM POPUP P
        WHERE MEMBER_ID = #{memberId}
    """)
    Optional<Long> selectPopupIdByMemberId(long memberId);
}
