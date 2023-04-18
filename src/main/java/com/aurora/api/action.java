package com.aurora.api;

import java.io.IOException;
import java.util.HashMap;
import org.jsoup.Jsoup;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.aurora.myJedis;
import com.aurora.utils.AuroraResult;
import com.aurora.utils.drewCode;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;


@RestController
@Controller
@Slf4j
public class action {
	static HashMap<Object, Object> points = new HashMap<>();
	/**
	 * 爬取网络
	 * @param request
	 * @param res
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/gethtml",method=RequestMethod.GET)
	public Object getKeywords(HttpServletRequest request,HttpServletResponse res,
	@RequestParam("url") String url) {
	        try {
	        	return Jsoup.connect(url).ignoreContentType(true).
	        			header("Content-Type", "application/json;charset=UTF-8").get().text();
	        }catch(Exception e) {
				log.error("/gethtml", e);
				return AuroraResult.fail("发生了一些错误！");
	        }
    }	
	/**
	 * 生成验证码
	 * @param request
	 * @param point
	 * @return
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value="/get/Codeimg/{id}",method=RequestMethod.GET)
	public Object UserLogin(HttpServletRequest request,HttpServletResponse response
			,@PathVariable("id") String id) throws Exception {
		Cookie cookies[] = request.getCookies() ;
		Cookie unit = null;
		HashMap<String, String> res = new HashMap<String, String>();
		if(cookies!=null) {
		for(Cookie c:cookies){
			String cookieName=c.getName();
			if(cookieName.equals("unitEasyAction")){
				unit = c;
			}
		}
		}

		if(unit==null) {
			unit = new Cookie("unitEasyAction","1");unit.setMaxAge(30);response.addCookie(unit);
		} else {
			if(Integer.valueOf(unit.getValue())>6) {
				res.put("status", "405");
				res.put("message", "访问频繁！限制访问！");
				return JSON.toJSONString(res);
			} else {
				unit.setValue(String.valueOf(Integer.valueOf(unit.getValue())+1));
				unit.setMaxAge(30);
				response.addCookie(unit);
			}
		}
		Jedis je = myJedis.getJedis();
		try {
			drewCode drawer = new drewCode();
			String code = drawer.getCode(4);
			je.setex("CODE_"+id, 120, code);
			ImageIO.write(drawer.creatImg(code,80, 30, 30), "JPG", response.getOutputStream());
			return null;
		} catch(Exception e) {
			res.put("status", "500");
			res.put("message", "BIG wrong!请反馈管理员");
			return JSON.toJSONString(res);
		} finally {
			je.close();
		}
    }
	static String parsePoint(String point){
		points.put("av", "videos");points.put("ns", "news");
		points.put("bl", "blogs");points.put("pt", "pictures");
		points.put("ur", "users");
		return  (String) points.get(point);
	}
}
