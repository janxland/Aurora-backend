package com.aurora.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.aurora.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
@Repository // 代表持久层

public interface AdminMapper extends BaseMapper<User>{

    public Map<String, Object> AdminLogin(User user) throws Exception;
    public List<Map> callSql(@Param("sql") String sql) throws Exception;
    public Map<String, String> AdminRegist(User user) throws Exception;
    public Map<String, String> actAdmins(@Param("fid") String wid,@Param("tid") String uid,@Param("act") String act) throws Exception;
    public int updateAdmin(@Param("id") String id,@Param("paramMap") Map paramMap) throws Exception;
    public int updateUserSafe(@Param("id") String id,@Param("paramMap") Map paramMap) throws Exception;
    public int setUserPassword(@Param("id") String id,@Param("fpassword") String fpassword,@Param("npassword") String npassword) throws Exception;
    /*
     * 插入作品
     * @param Work
     * @param id
     * @return
     * @throws Exception
     */
    public int createWork(@Param("extraMap") Map extraMap,@Param("paramMap") Map paramMap) throws Exception;
  
    /*
     * 修改作品
     * @param Work
     * @param id
     * @return
     * @throws Exception
     */
    public int updateWork(@Param("point") String point ,@Param("wid") String wid,@Param("paramMap") Map paramMap) throws Exception;
    public int deleteWork(@Param("extraMap") Map extraMap) throws Exception;
    public int hideWork(@Param("point") String point,@Param("paramMap") Map paramMap) throws Exception;
}