package com.aurora.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;


@TableName("users")
@Data
public class User implements Serializable{
	private String id= null;
	private String password=null;
    private String name=null;
    private String poster=null;
    private int fans = 0;
    private int sex=0;
    private int age=0;
    private String ss=null;
    @TableField(exist = false)
    private int group = 0;
    private int permission = 0;
    private Date time = null;
    public User(String Id,String Password){
        this.setId(Id);
        this.password=Password;
    }
	public User(){}
}