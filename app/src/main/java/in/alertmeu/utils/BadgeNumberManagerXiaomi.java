package in.alertmeu.utils;

import android.app.Notification;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BadgeNumberManagerXiaomi {
    public static void setBadgeNumber(Notification notification, int number) {
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
