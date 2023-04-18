package com.aurora.api;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.aurora.error;
import com.aurora.myJedis;
import com.aurora.mybatis;
import com.aurora.mapper.GetMapper;
import com.aurora.utils.AuroraCache;
import com.aurora.utils.AuroraResult;

import redis.clients.jedis.Jedis;


@RestController
@Controller
public class get {
	static HashMap<Object, Object> points = new HashMap<>();
	
	/**
	 * 获取总数目
	 * @param request
	 * @param point
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get/num/total/{point}",method=RequestMethod.GET)
	public Object getTotal(HttpServletRequest request,@PathVariable("point") String point) {  
		//得到连接对象
        SqlSession sqlSession = mybatis.getSession();
        Object obj = null ;
        try{
        	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
        	obj = mapper.getTotal(parsePoint(point),request.getParameter("key"));
            sqlSession.commit();
        }catch(Exception e){
        	System.err.println(e);
        }finally{
        	mybatis.closeSession();
        }
		return JSON.toJSONString(obj);
    }
	/**
	 * 一般获取
	 * @param request
	 * @param point
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get/works/{point}",method=RequestMethod.GET)
	public Object getShow(HttpServletRequest request,
	@PathVariable("point") String point,
	@RequestParam(value="type",required=true,defaultValue = "1") int type,
	@RequestParam(value="start",required=true,defaultValue = "1") int start,
	@RequestParam(value="num",required=true,defaultValue = "10") int num) {
		String cache_key = "gw"+request.getParameter("num")+"_"+point+"_"+request.getParameter("type");
		String obj = AuroraCache.getJedisChache(cache_key);
		if(obj!=null) {
			return obj;
		} else {
			SqlSession sqlSession = mybatis.openSession();
			try{
				int seconds = 60;
				int time_table[] = {0,60,43200,20};//最新 最火 随机
				seconds = time_table[type];
				GetMapper mapper = sqlSession.getMapper(GetMapper.class);
				obj = JSON.toJSONString(mapper.getShow(point,type,start,num));
				sqlSession.commit();
				AuroraCache.setJedisChache(cache_key, obj,seconds);
			}catch(Exception e){
				new error(e);
			} finally {
				mybatis.closeSession(sqlSession);
			}
		}
		return  obj;
    }
	/**
	 * 搜索作品
	 * @param request
	 * @param point
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/search/works/{point}",method=RequestMethod.GET)
	public Object searchWork(HttpServletRequest request,
	@PathVariable("point") String point,
	@RequestParam(value="type",required=true,defaultValue = "1") int type,
	@RequestParam(value="start",required=true,defaultValue = "1") int start,
	@RequestParam(value="num",required=true,defaultValue = "10") int num,
	@RequestParam(value="keys",required=true,defaultValue = "") String keys) {  
        Object obj = null ;
        SqlSession sqlSession = mybatis.getSession();
        try{
        	point = parsePoint(point);
        	if(point!=null) {
				int author = 0;
				if(request.getParameter("author")!=null){
					author = Integer.valueOf(request.getParameter("author"));
				}
	        	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
	        	obj = mapper.searchWork(point,type,start,num,author,keys);
	        	sqlSession.commit();
        	}
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        	mybatis.closeSession();
        }
		return JSON.toJSONString(obj);
    }
	// /**
	//  * 获取外展卡Byids
	//  * @param request
	//  * @param point
	//  * @return
	//  */
	// @ResponseBody
	// @RequestMapping(value="/get/works/ids/{point}",method=RequestMethod.GET)
	// public Object getShowByIds(HttpServletRequest request,@PathVariable("point") String point) {  
	// 	//得到连接对象
    //     SqlSession sqlSession = mybatis.getSession();
    //     Object obj = null ;
    //     try{
    //     	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
    //     	obj = mapper.getShowByIds(point, request.getParameter("ids"));
    //         sqlSession.commit();
    //     }catch(Exception e){
        	
    //     }finally{
    //     	mybatis.closeSession();
    //     }
	// 	return JSON.toJSONString(obj);
    // }

	/**
	 * 获取详细卡(全文获取)
	 * @param request
	 * @param point
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get/work/{point}/{id}",method=RequestMethod.GET)
	public Object getWork(HttpServletRequest request,@PathVariable("point") String point,@PathVariable("id") String id) {  
        SqlSession sqlSession = mybatis.getSession();
        Object obj = null ;
        try{
        	point = parsePoint(point);
        	if(point!=null&&!point.equals("users")) {
	        	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
	        	obj = mapper.getWork(point,id);
	        	mapper.clickWork(point,id);
	            sqlSession.commit();
        	}
        }catch(Exception e){
            e.printStackTrace();
            sqlSession.rollback();
        }finally{
        	mybatis.closeSession();
        }
		return JSON.toJSONString(obj);
    }

	
	/**
	 * 获取用户
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get/user/{id}",method=RequestMethod.GET)
	public Object getUser(HttpServletRequest request,@PathVariable("id") Integer id) {  
		//得到连接对象
        SqlSession sqlSession = mybatis.getSession();
        HashMap obj = null ;
        try{
        	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
        	obj = mapper.getUser(id);
        	obj.put("password", "");
            sqlSession.commit();
        }catch(Exception e){
            e.printStackTrace();
            sqlSession.rollback();
        }finally{
        	mybatis.closeSession();
        }
		return JSON.toJSONString(obj);
    }	

	/**
	 * 获取用户属性
	 * @param request
	 * @param id
	 * @param act
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get/attr/user/{id}/{act}",method=RequestMethod.GET)
	public Object getUserAttr(HttpServletRequest request,@PathVariable("id") Integer id,@PathVariable("act") String act) {  
        SqlSession sqlSession = mybatis.getSession();
        HashMap obj = null ;
        try{
        	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
        	obj = mapper.getUserAttr(id, act);
            sqlSession.commit();
        }catch(Exception e){
            e.printStackTrace();
            sqlSession.rollback();
        }finally{
        	mybatis.closeSession();
        }
		return JSON.toJSONString(obj);
    }	

	/**
	 * 获取评论
	 * @param request
	 * @param id
	 * @param act
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get/comments/{type}/{wid}/{fid}/{start}/{num}",method=RequestMethod.GET)
	public Object getComments(HttpServletRequest request,
			@PathVariable("type") int type,
			@PathVariable("wid") String wid ,
			@PathVariable("fid") String fid ,
			@PathVariable("start") int start ,
			@PathVariable("num") int num ) {
        SqlSession sqlSession = mybatis.getSession();
        Object obj = null ;
        try{
        	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
        	obj = mapper.getComments(type, wid, fid, start, num);
            sqlSession.commit();
        }catch(Exception e){
            e.printStackTrace();
            sqlSession.rollback();
        }finally{
        	mybatis.closeSession();
        }
		return JSON.toJSONString(obj);
    }	
	
	/**
	 * 获取弹幕
	 * @param request
	 * @param id
	 * @param act
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get/danmus/{wid}/{start}/{num}",method=RequestMethod.GET)
	public Object getDanmus(HttpServletRequest request,
			@PathVariable("wid") String wid ,
			@PathVariable("start") int start ,
			@PathVariable("num") int num ) { 
        SqlSession sqlSession = mybatis.getSession();
        Object obj = null ;
        try{
        	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
        	obj = mapper.getDanmus(wid, start, num);
            sqlSession.commit();
        }catch(Exception e){
            e.printStackTrace();
            sqlSession.rollback();
        }finally{
        	mybatis.closeSession();
        }
		return JSON.toJSONString(obj);
    }	
	/**
	 * 获取热搜
	 * @param request
	 * @param key
	 * @param start
	 * @param num
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get/keywords/{start}/{num}",method=RequestMethod.GET)
	public Object getHotKeys(HttpServletRequest request,
			@PathVariable("start") int start ,
			@PathVariable("num") int num ) {
		Object obj = null ;
		String cache_key = "topKeys"+start+"_"+num;
		Jedis je = myJedis.getJedis();
		if(je.exists(cache_key)) {
			obj = je.get(cache_key);
			je.close();
			return (String) obj;
		}
        SqlSession sqlSession = mybatis.getSession();
        try{
        	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
        	obj = JSON.toJSONString(mapper.getKeywords("", start, num));
            sqlSession.commit();
            je.setex(cache_key,43200, (String) obj);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        	je.close();
        	mybatis.closeSession();
        }
		return (String) obj;
    }	
	
	/**
	 * 获取搜索关键词
	 * @param request
	 * @param key
	 * @param start
	 * @param num
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get/keywords/{start}/{num}/{key}",method=RequestMethod.GET)
	public Object getKeywords(HttpServletRequest request,
			@PathVariable("start") int start ,
			@PathVariable("num") int num ,
			@PathVariable("key") String key) {
        SqlSession sqlSession = mybatis.getSession();
        Object obj = null ;
        try{
        	GetMapper mapper = sqlSession.getMapper(GetMapper.class);
        	obj = JSON.toJSONString(mapper.getKeywords(key, start, num));
            sqlSession.commit();
        }catch(Exception e){
            e.printStackTrace();
            sqlSession.rollback();
        }finally{
        	mybatis.closeSession();
        }
		return (String) obj;
    }	
	
	static String parsePoint(String point){
		points.put("av", "videos");points.put("ns", "news");
		points.put("bl", "blogs");points.put("pt", "pictures");
		points.put("ur", "users");points.put("ct", "comments");
		points.put("dm", "danmus");
		return  (String) points.get(point);
	}
}
