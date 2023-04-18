package com.aurora.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface GetMapper {

    /*
     * 查询
     * @param Work
     * @return
     * @throws Exception
     */
	public int getTotal(@Param("point") String point,@Param("key") String key);
    public List<HashMap<String,Object>> getShow(@Param("point") String point,@Param("type") Integer type,
    		@Param("start")Integer start,@Param("num")Integer num) throws Exception;
    public List<HashMap<String,Object>> getShowByIds(@Param("point")String point,@Param("ids")String ids);
    public List<?> searchWork(@Param("point") String point,@Param("type") int type,
    		@Param("start")Integer start,@Param("num")Integer num,@Param("author")Integer author,@Param("keys")String keys) throws Exception;
	public List<HashMap<String,Object>> getKeywords(@Param("key") String key,@Param("start")int start,@Param("num") int num);
    public void setKeywords(@Param("key") String key);
	public int clickWork(@Param("point") String point,@Param("id") String id);
    /*
     * 查询X
     * @param Work
     * @return
     * @throws Exception
     */
//    public List<HashMap<String,Object>> getXShow(@Param("point") String point,@Param("type") String type,
//    		@Param("start")Integer start,@Param("num")Integer num,@Param("adm")Integer adm) throws Exception;

	public HashMap<String,Object> getWork(@Param("point") String point,@Param("id") String id);
	public HashMap<String,Object> getUser(@Param("id") Integer id);
	public HashMap<String,Object> getUserAttr(@Param("id") Integer id,@Param("act") String act);
	public List<HashMap<String,Object>> getComments(@Param("type") int type ,@Param("wid") String wid,@Param("fid") String fid,@Param("start")int start,@Param("num")int num);
	public List<HashMap<String,Object>> getDanmus(@Param("wid") String wid,@Param("start")int start,@Param("num") int num);
}