package com.aurora.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.aurora.JwtUtil;
import com.aurora.mybatis;
import com.aurora.config.LoginCheck;
import com.aurora.mapper.WorkMapper;
import com.aurora.utils.AuroraResult;
import com.aurora.utils.AuroraUtil;

import io.jsonwebtoken.Claims;

@RestController
@Controller
public class work {
	static HashMap<Object, Object> points = new HashMap<>();
	static HashMap<Object, Object> columns = new HashMap<>();
	/**
	 * 新增作品
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck
	@ResponseBody
	@RequestMapping(value="/insert/work/{point}",method=RequestMethod.POST)
	public Object createWork(HttpServletRequest request,HttpServletResponse response,@PathVariable("point") String point) throws Exception {
		Claims token = JwtUtil.parseJWT(request.getHeader("Authorization"));
		if(request.getHeader("Authorization")==null||token==null) {
			return "{states:'404','msg':'无效签名！'}";
		}
		
		HashMap<String, String> res = new HashMap<String, String>();
		Map<String, String[]> paramMap =  request.getParameterMap();
		Map<String, Object> paramMaps =  new HashMap<String, Object>();
		Map<String, Object> extraMaps =  new HashMap<String, Object>();
		paramMap.forEach((k,v)->{
			String pc = parseColumn(k);
			if(pc!=null) {
				if(v[0]!=null)paramMaps.put(pc, v[0]);
			}
		});
		if(paramMaps.isEmpty()) return "{states:'500','msg':'无参数！'}";
		paramMaps.put("author", (String)token.get("uid"));
		SqlSession sqlSession = mybatis.getSession();
		 try {
			extraMaps.put("point", parsePoint(point));
			extraMaps.put("pint", point);
			WorkMapper mapper = sqlSession.getMapper(WorkMapper.class);
			int obj = mapper.createWork(extraMaps,paramMaps);
		  	sqlSession.commit();
		  	if(obj==1) {
		  		res.put("states", "0");
		  		res.put("msg", "新增成功!");
		  	} else {
		  		res.put("states", "500");
		  		res.put("msg", "新增失败!请检查账号密码");
		  	}
		  	return JSON.toJSONString(res);
		 }catch(Exception e){
		    e.printStackTrace();
		    sqlSession.rollback();
		  	res.put("states", "500");
		  	res.put("msg", "严重错误!");
		  	return JSON.toJSONString(res);
		 }finally{
		     mybatis.closeSession();
		 }
    }
	/**
	 * 修改作品
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck
	@ResponseBody
	@RequestMapping(value="/set/work/{point}/{wid}",method=RequestMethod.POST)
	public Object updateWork(HttpServletRequest request,
		@PathVariable("point") String point,
		@PathVariable("wid") String wid) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();
		Map<String, String[]> paramMap =  request.getParameterMap();
		Map<String, Object> paramMaps =  new HashMap<String, Object>();
		paramMap.forEach((k,v)->{
			String pc = parseColumn(k);
			if(pc!=null) {
				if(!v[0].equals("")&&v[0]!=null)paramMaps.put(pc, v[0]);
			}
		});
		if(paramMaps.isEmpty()) return AuroraResult.fail("参数不够");
		SqlSession sqlSession = mybatis.getSession();
		 try {
			WorkMapper mapper = sqlSession.getMapper(WorkMapper.class);
			int obj = mapper.updateWork(parsePoint(point),wid,AuroraUtil.getUid(),paramMaps);
		  	sqlSession.commit();
		  	if(obj==1) {
		  		res.put("states", "0");
		  		res.put("msg", "修改成功!");
		  	} else {
		  		res.put("states", "500");
		  		res.put("msg", "修改失败!请检查账号密码");
		  	}
		  	return JSON.toJSONString(res);
		 }catch(Exception e){
		    e.printStackTrace();
		    sqlSession.rollback();
		  	res.put("states", "500");
		  	res.put("msg", "严重错误!");
		  	return JSON.toJSONString(res);
		 }finally{
		     mybatis.closeSession();
		 }
    }
	/**
	 * 删除作品（隐藏）
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck
	@ResponseBody
	@RequestMapping(value="/del/work/{point}/{wid}",method=RequestMethod.POST)
	public Object deleteWork(HttpServletRequest request,HttpServletResponse response,@PathVariable("point") String point,
			@PathVariable("wid") String wid) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();
		Map<String, String[]> paramMap =  request.getParameterMap();
		Map<String, Object> paramMaps =  new HashMap<String, Object>();
		paramMap.forEach((k,v)->{
			if(parseColumn(k)!=null) {
				paramMaps.put(parseColumn(k), v[0]);
			}
		});
		Claims token = JwtUtil.parseJWT(request.getHeader("Authorization"));
		paramMaps.put("author", (String)token.get("uid"));
		SqlSession sqlSession = mybatis.getSession();
		 try {
			WorkMapper mapper = sqlSession.getMapper(WorkMapper.class);
			int obj = mapper.deleteWork(paramMaps);
		  	sqlSession.commit();
		  	if(obj==1) {
		  		res.put("states", "0");
		  		res.put("msg", "删除成功!");
		  	} else {
		  		res.put("states", "500");
		  		res.put("msg", "删除失败!请检查账号密码");
		  	}
		  	return JSON.toJSONString(res);
		 }catch(Exception e){
		    e.printStackTrace();
		    sqlSession.rollback();
		  	res.put("states", "500");
		  	res.put("msg", "严重错误!");
		  	return JSON.toJSONString(res);
		 }finally{
		     mybatis.closeSession();
		 }
    }
	
	/**
	 * 点赞 投币 收藏 ----操作
	 * @param request
	 * @param response
	 * @param point
	 * @param wid
	 * @return
	 * @throws Exception
	 */
	@LoginCheck
	@ResponseBody
	@RequestMapping(value="/act/work/{point}/{wid}/{act}",method=RequestMethod.POST)
	public Object actWork(HttpServletRequest request,HttpServletResponse response,@PathVariable("point") String point,
			@PathVariable("wid") String wid,@PathVariable("act") String act) throws Exception {
		Claims token = JwtUtil.parseJWT(request.getHeader("Authorization"));
		if(request.getHeader("Authorization")==null||token==null) {
			return "{states:'404','msg':'无效签名！'}";
		}
		Map<String, Object> res = new HashMap<String, Object>();
		SqlSession sqlSession = mybatis.getSession();
		String uid = (String) token.get("uid");
		try {
			WorkMapper mapper = sqlSession.getMapper(WorkMapper.class);
			Map obj = mapper.actWork(parsePoint(point),wid, uid, act);
		  	sqlSession.commit();
		  	res.put("states", "0");
		  	res.put("res", obj);
		  	res.put("msg", "请求成功！");
		  	return JSON.toJSONString(res);
		}catch(Exception e){
			e.printStackTrace();
			sqlSession.rollback();
			res.put("states", "500");
			res.put("msg", "严重错误!");
		}finally{
		     mybatis.closeSession();
		}
		return JSON.toJSONString(res);
		
    }	
	
	
	
	/**
	 * 新增评论
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck
	@ResponseBody
	@RequestMapping(value="/insert/comment",method=RequestMethod.POST)
	public Object createComment(HttpServletRequest request) throws Exception {
		Map<String, Object> res = new HashMap<String, Object>();
		Claims token = JwtUtil.parseJWT(request.getHeader("Authorization"));
		SqlSession sqlSession = mybatis.getSession();
		 try {
			WorkMapper mapper = sqlSession.getMapper(WorkMapper.class);
			Map obj = mapper.createComment(request.getParameter("wid"),request.getParameter("reply")
					,(String) token.get("uid"),request.getParameter("content"));
		  	sqlSession.commit();
		  	res.put("res", obj);
	  		res.put("states", "0");
	  		res.put("msg", "操作成功!");
		  	return JSON.toJSONString(res);
		 }catch(Exception e){
		    e.printStackTrace();
		    sqlSession.rollback();
		  	res.put("states", "500");
		  	res.put("msg", "严重错误!");
		  	return JSON.toJSONString(res);
		 }finally{
		     mybatis.closeSession();
		 }
    }
	
	/**
	 * 新增弹幕
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck
	@ResponseBody
	@RequestMapping(value="/insert/danmu",method=RequestMethod.POST)
	public Object createDanmu(HttpServletRequest request) throws Exception {
		Map<String, Object> res = new HashMap<String, Object>();
		Claims token = JwtUtil.parseJWT(request.getHeader("Authorization"));
		SqlSession sqlSession = mybatis.getSession();
		 try {
			WorkMapper mapper = sqlSession.getMapper(WorkMapper.class);
			Map obj = mapper.createDanmu(request.getParameter("wid"),(String) token.get("uid")
					,request.getParameter("content"),request.getParameter("attr"));
		  	sqlSession.commit();
		  	res.put("res", obj);
	  		res.put("states", "0");
	  		res.put("msg", "操作成功!");
		  	return JSON.toJSONString(res);
		 }catch(Exception e){
		    e.printStackTrace();
		    sqlSession.rollback();
		  	res.put("states", "500");
		  	res.put("msg", "严重错误!");
		  	return JSON.toJSONString(res);
		 }finally{
		     mybatis.closeSession();
		 }
    }
	static String parseColumn(String point){
//		columns.put("id", "id");
//		columns.put("author", "author");
		columns.put("poster", "poster");
		columns.put("title", "title");
		columns.put("content", "content");
		columns.put("keywords", "keywords");
		columns.put("permission", "permission");
		columns.put("picture", "picture");
		columns.put("url", "url");
		columns.put("prectx", "prectx");
//		columns.put("like", "like");
//		columns.put("bi", "bi");
//		columns.put("plays", "plays");
//		columns.put("collect", "collect");
//		columns.put("time", "time");
		return  (String) columns.get(point);
	}
	
	static String parsePoint(String point){
		points.put("av", "videos");points.put("ns", "news");
		points.put("bl", "blogs");points.put("pt", "pictures");
		points.put("ur", "users");points.put("ct", "comments");
		points.put("dm", "danmus");
		return  (String) points.get(point);
	}
}
