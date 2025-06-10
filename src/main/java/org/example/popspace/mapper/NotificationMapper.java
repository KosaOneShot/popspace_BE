package org.example.popspace.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.popspace.dto.notification.NotificationResponseDto;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NotificationMapper {
    @Select("SELECT SEQ_NOTIFY_ID.NEXTVAL FROM DUAL")
    long getNextNotifyId();

    @Insert("""
        INSERT INTO NOTIFICATION (
            NOTIFY_ID,
            POPUP_ID,
            TITLE,
            CONTENT,
            IMAGE_URL,
            NOTIFICATION_STATE,
            CREATED_AT
        ) VALUES (
            #{notifyId},
            #{popupId},
            #{title},
            #{content},
            #{imageUrl},
            #{notificationState},
            SYSDATE
        )
    """)
    int insertNotification(NotificationResponseDto notification);

    @Select("""
        SELECT DISTINCT N.*
        FROM NOTIFICATION N
                 JOIN RESERVATION R ON N.popup_id = R.popup_id
                 JOIN MEMBER M ON R.member_id = M.member_id
        WHERE R.MEMBER_ID = #{memberId}
                  AND R.reservation_state = 'RESERVED'
                  AND N.notification_state = 'ACTIVE'
    """)
    List<NotificationResponseDto> selectNotificationsByMemberId(long memberId);

    @Select("""
        SELECT MEMBER_ID
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
