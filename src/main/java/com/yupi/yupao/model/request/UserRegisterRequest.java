package com.yupi.yupao.model.request;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;
    private String userName;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
    private String gender;
    private String phone;
    private String email;
}
