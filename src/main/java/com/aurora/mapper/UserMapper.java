package com.aurora.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.aurora.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
@Repository // 代表持久层

public interface UserMapper extends BaseMapper<User>{

    public Map<String, Object> UserLogin(User User) throws Exception;
    
    public HashMap<String, String> UserRegist(User User) throws Exception;
    public Map actUsers(@Param("fid") String wid,@Param("tid") String uid,@Param("act") String act) throws Exception;
    public int updateUser(@Param("id") String id,@Param("paramMap") Map paramMap) throws Exception;
    public int updateUserSafe(@Param("id") String id,@Param("paramMap") Map paramMap) throws Exception;
    public int setUserPassword(@Param("id") String id,@Param("fpassword") String fpassword,@Param("npassword") String npassword) throws Exception;
}