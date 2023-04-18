package com.aurora.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;


public interface WorkMapper {

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
    public int updateWork(@Param("point") String point ,@Param("wid") String wid,@Param("uid") String uid,@Param("paramMap") Map paramMap) throws Exception;
  
  
    public int deleteWork(@Param("paramMap") Map paramMap) throws Exception;
    public Map actWork(@Param("point") String point,@Param("wid") String wid,@Param("uid") String uid,@Param("act") String act) throws Exception;
    public Map createComment(@Param("wid")String wid,@Param("reply")String reply,@Param("author")String author,@Param("content")String content);

	public Map createDanmu(@Param("wid")String wid,@Param("uid") String uid, @Param("content")String content, @Param("attr") String attr);
}