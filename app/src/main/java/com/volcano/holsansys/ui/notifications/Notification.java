package com.volcano.holsansys.ui.notifications;

public class Notification {

    private String time_notification;
    private String remark_notification;
    private String switch_notification;

    public Notification(String time_notification, String remark_notification, String switch_notification) {
        this.time_notification = time_notification;
        this.remark_notification = remark_notification;
        this.switch_notification = switch_notification;
    }

    public String getTime() {
        return time_notification;
    }

    public String getRemark() {
        return remark_notification;
    }

    public String getSwitch() {
    return switch_notification;
}

    public void setTime(String time_notification) {
        this.time_notification = time_notification;
    }

    public void setRemark(String remark_notification) {
        this.remark_notification = remark_notification;
    }

    public void setSwitch(String switch_notification) {
    this.switch_notification = switch_notification;
}

}
