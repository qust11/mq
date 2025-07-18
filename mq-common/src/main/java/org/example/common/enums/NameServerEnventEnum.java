package org.example.common.enums;


import lombok.Getter;

/**
 * @author qushutao
 * @since 2025/7/16 11:34
 **/
@Getter
public enum NameServerEnventEnum {
    REGISTER(1, "注册"),
    HEART_BEAT(2, "心跳"),
    UNREGISTER(3, "注销"),
    /******************** 异常状态  **********************/
    USER_PASSWORD_ERROR(10001,"用户名密码错误"),
    REQ_ID_MISS_ERROR(10002, "请求ID丢失")

    ;

    private int code;
    private String desc;

    NameServerEnventEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
