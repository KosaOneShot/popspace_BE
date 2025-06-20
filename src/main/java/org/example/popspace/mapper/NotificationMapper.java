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
        SELECT N.NOTIFY_ID,
               N.POPUP_ID,
               N.TITLE,
               N.CONTENT,
               N.IMAGE_URL,
               N.CREATED_AT,
               N.NOTIFICATION_STATE
        FROM NOTIFICATION N
                 JOIN RESERVATION R ON N.popup_id = R.popup_id AND R.MEMBER_ID = #{memberId}
                  AND R.reservation_state IN ('RESERVED', 'CHECKED_IN', 'EMAIL_SEND')
        WHERE N.notification_state = 'ACTIVE'
    """)
    List<NotificationResponseDto> selectNotificationsByMemberId(long memberId);

    @Select("""
        SELECT DISTINCT MEMBER_ID
        FROM RESERVATION R
        WHERE POPUP_ID = #{popupId}
            AND RESERVATION_STATE IN ('RESERVED', 'CHECKED_IN', 'EMAIL_SEND')
    """)
    List<Long> selectReservedMemberIds(long popupId);
}
