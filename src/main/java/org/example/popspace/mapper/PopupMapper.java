package org.example.popspace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface PopupMapper {

    @Select("""
    SELECT member_id
    FROM popup
    WHERE popup_id = #{popupId}
    """)
    Optional<Long> findPopupOwnerIdByPopupId(Long popupId);

}
