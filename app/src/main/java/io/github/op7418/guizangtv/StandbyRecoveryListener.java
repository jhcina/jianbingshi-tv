package io.github.op7418.guizangtv;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/** System rebind only. Never calls getActiveNotifications or inspects a notification. */
public final class StandbyRecoveryListener extends NotificationListenerService {
    private static StandbyRecoveryListener connected;
    @Override public void onListenerConnected() {
        connected = this;
        StandbyRecovery.recover(this);
    }
    @Override public void onNotificationPosted(StatusBarNotification ignored) { }
    @Override public void onNotificationRemoved(StatusBarNotification ignored) { }
    static void unbind() {
        if (connected != null) connected.requestUnbind();
    }
    @Override public void onDestroy() {
        if (connected == this) connected = null;
        super.onDestroy();
    }
}
