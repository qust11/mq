package org.example.nameserver.event.model;


import lombok.Data;

/**
 * @author qushutao
 * @since 2025/7/16 11:33
 **/
@Data
public class RegisterEvent extends  Event{

    private String brokerIp;

    private int brokerPort;

    private String username;

    private String password;
}
