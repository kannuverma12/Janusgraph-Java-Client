package com.paytm.digital.education.service.notification;

import static com.paytm.digital.education.constant.DBConstants.SUCCESS;

import com.paytm.digital.education.database.entity.UserFlags;
import com.paytm.digital.education.database.repository.UserFlagRepository;
import com.paytm.digital.education.dto.NotificationFlags;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImpl {

    private UserFlagRepository userFlagRepository;

    private static NotificationFlags DEFAULT_SUCCESS_FLAGS = new NotificationFlags(SUCCESS);

    static {
        DEFAULT_SUCCESS_FLAGS.setUnreadShortlist(0);
    }

    public NotificationFlags getNotificationFlags(long userId) {
        UserFlags userFlags = userFlagRepository.getUserFlag(userId);
        if (userFlags != null && userFlags.getUnreadShortlistCount() != null) {
            NotificationFlags notificationFlags = new NotificationFlags(SUCCESS);
            notificationFlags.setUnreadShortlist(userFlags.getShortlistFlag());
            return notificationFlags;
        }
        return DEFAULT_SUCCESS_FLAGS;
    }

}
