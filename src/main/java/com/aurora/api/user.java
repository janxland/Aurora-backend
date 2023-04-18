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
import com.aurora.error;
import com.aurora.myJedis;
import com.aurora.mybatis;
import com.aurora.config.LoginCheck;
import com.aurora.entity.User;
import com.aurora.mapper.UserMapper;
import com.aurora.utils.AuroraResult;
import com.aurora.utils.AuroraUtil;

import io.jsonwebtoken.Claims;
import redis.clients.jedis.Jedis;

@RestController
@Controller
public class user {
	private static Map<String, String> columns = new HashMap<>();
	/**
	 * 用户登录
	 * @param request
	 * @param point
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/action/user/login",method=RequestMethod.POST)
	public Object UserLogin(HttpServletRequest request) { 
		//得到连接对象
        SqlSession sqlSession = mybatis.getSession();
        Map<String, Object> obj = null ;
        Map<String,Object> res = new HashMap<String, Object>();
        Jedis je = myJedis.getJedis();
        try{
            // String code = je.get("CODE_"+request.getParameter("id"));
            // if (code==null||request.getParameter("code")==null||!code.equals(request.getParameter("code").toUpperCase())) {
            // 	res.put("states", "400");
			// 	res.put("msg", "验证码错误！Code error!");
            // 	return JSON.toJSONString(res);
            // }
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            User user = new User(request.getParameter("id"),request.getParameter("password"));
            obj = mapper.UserLogin(user);
            sqlSession.commit();
            if(obj!=null) {
            	//je.del("CODE_"+request.getParameter("id"));
            	obj.remove("password");     
				user.setPermission(Integer.parseInt(obj.get("permission").toString()));
				res.put("states", "0");
				res.put("token", JwtUtil.createJWT(6048000, user));
				res.put("user", obj);
            } else {
				res.put("states", "500");
				res.put("msg", "create token failed.Check your input keys.");
            }
            
        }catch(Exception e){
			new error(e);
            e.printStackTrace();
            sqlSession.rollback();
        }finally{
        	je.close();
            mybatis.closeSession();
        }
		return JSON.toJSONString(res);
    }
	/**
	 * 注册
	 * @param request
	 * @param point
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/action/user/regist",method=RequestMethod.POST)
	public Object UserRegist(HttpServletRequest request) {
		//得到连接对象
        SqlSession sqlSession = mybatis.getSession();
        Map obj = null ;
        Map<String,Object> res = new HashMap<String, Object>();
        Jedis je = myJedis.getJedis();
        try{
            String code = je.get("CODE_"+request.getParameter("id")); //id为标识
            if (code==null||request.getParameter("code")==null||!code.equals(request.getParameter("code").toUpperCase())) {
            	return "code wrong!!!";
            }
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            User user = new User();
            user.setPassword(request.getParameter("password"));
            user.setName(request.getParameter("name"));
            obj = mapper.UserRegist(user);
            sqlSession.commit();
            if(obj!=null) {
            	je.del("CODE_"+request.getParameter("id"));
            	obj.remove("password");
            	res.put("states", "0");
            	user.setId(String.valueOf(obj.get("id")));
            	res.put("user", obj);
				res.put("token", JwtUtil.createJWT(6048000, user));
            } else {
				res.put("res", "500");
				res.put("msg", "create token failed.Check your input keys.");
            }
            
        }catch(Exception e){
            e.printStackTrace();
            sqlSession.rollback();
        }finally{
        	je.close();
            mybatis.closeSession();
        }
		return JSON.toJSONString(res);
    }
	
	/**
	 * 关注 收藏
	 * @param request
	 * @param response
	 * @param point
	 * @param wid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/act/user/{tid}/{act}",method=RequestMethod.POST)
	public Object actWork(HttpServletRequest request,HttpServletResponse response,
			@PathVariable("tid") String tid,@PathVariable("act") String act) throws Exception {
		Claims token = JwtUtil.parseJWT(request.getHeader("Authorization"));
		if(request.getHeader("Authorization")==null||token==null) {
			return "{states:'404','msg':'无效签名！'}";
		}
		Map<String, Object> res = new HashMap<String, Object>();
		SqlSession sqlSession = mybatis.getSession();
		String fid = (String) token.get("uid");
		try {
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			Map obj = mapper.actUsers(fid, tid, act);
		  	sqlSession.commit();
		  	res =  obj;
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
	 * 修改用户
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck
	@ResponseBody
	@RequestMapping(value="/set/user",method=RequestMethod.POST)
	public Object updateUser(HttpServletRequest request) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();
		Map<String, String[]> paramMap =  request.getParameterMap();
		Map<String, Object> paramMaps =  new HashMap<String, Object>();
		paramMap.forEach((k,v)->{
			String pc = parseColumn(k);
			if(pc!=null&&!pc.equals("password")) {
				if(!v[0].equals("")&&v[0]!=null)paramMaps.put(pc, v[0]);
			}
		});
		if(paramMaps.isEmpty()) return new AuroraResult<>(500,"无需要修改信息！");
		SqlSession sqlSession = mybatis.getSession();
		 try {
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			int obj = mapper.updateUser(AuroraUtil.getUid(),paramMaps);
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
	 * 修改用户密码
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value="/set/userpassword",method=RequestMethod.POST)
	public Object setUserPassword(HttpServletRequest request) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();
		SqlSession sqlSession = mybatis.getSession();
		 try {
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			int obj = mapper.setUserPassword(AuroraUtil.getUid(), 
					request.getParameter("fpassword"),request.getParameter("npassword"));
		  	sqlSession.commit();
		  	System.out.println(obj);
		  	if(obj==0) {
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
	 * 修改用户安全表
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@LoginCheck
	@ResponseBody
	@RequestMapping(value="/set/safe/user",method=RequestMethod.POST)
	public Object updateUserSafe(HttpServletRequest request) throws Exception {
		Map<String, String> columnsa = new HashMap<String, String>();
		columnsa.put("email", "email");
		columnsa.put("qq", "qq");
		columnsa.put("phone", "phone");
		HashMap<String, String> res = new HashMap<String, String>();
		Map<String, String[]> paramMap =  request.getParameterMap();
		Map<String, Object> paramMaps =  new HashMap<String, Object>();
		paramMap.forEach((k,v)->{
			String pc = (String) columnsa.get(k);
			if(pc!=null) {
				if(!v[0].equals("")&&v[0]!=null)paramMaps.put(pc, v[0]);
			}
		});
		if(paramMaps.isEmpty()) return "{states:'500','msg':'无参数！'}";
		SqlSession sqlSession = mybatis.getSession();
		 try {
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			int obj = mapper.updateUserSafe(AuroraUtil.getUid(),paramMaps);
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
	

	static String parseColumn(String point){
		columns.put("poster", "poster");
		columns.put("name", "name");
		columns.put("password", "password");
		columns.put("age", "age");
		columns.put("sex", "sex");
		columns.put("ss", "ss");
		return  columns.get(point);
	}
	
	
}
