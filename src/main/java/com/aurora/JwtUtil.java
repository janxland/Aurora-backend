package com.aurora;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson2.JSON;
import com.aurora.entity.User;
import com.aurora.utils.CommonConst;

/**
 * @Author:jimisun
 * @Description:
 * @Date:Created in 14:08 2018/8/15
 * @Modified By:
 */
public class JwtUtil {
	static String key = CommonConst.JWT_KEY;
    /**
     * 用户登录成功后生成Jwt
     * 使用Hs256算法  私匙使用用户密码
     *
     * @param ttlMillis jwt过期时间
     * @param user      登录成功的user对象
     * @return
     */
    public static String createJWT(int second, User user) {
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, second);
        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("uid", user.getId());
        claims.put("permission", user.getPermission());
        HashMap<String, String> summary = new HashMap<>();
        //生成签发人
        summary.put("uid",String.valueOf(user.getId()));
        String subject = JSON.toJSONString(summary);
        //下面就是在为payload添加各种标准声明和私有声明了
        //这里其实就是new一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUID.randomUUID().toString())
                //iat: jwt的签发时间
                .setIssuedAt(now)
                //代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, key);
        if (second >= 0) {
            builder.setExpiration(instance.getTime());
        }
        return builder.compact();
    }


    /**
     * Token的解密
     * @param token 加密后的token
     * @return
     */
    public static Claims parseJWT(String token) {
    	JwtParser jwt = Jwts.parser();
    	if(!jwt.isSigned(token)) return null;
        //得到DefaultJwtParser
    	Claims claims =null;
    	try {
    		claims = jwt
    				//设置签名的秘钥
    				.setSigningKey(key)
    				//设置需要解析的jwt
    				.parseClaimsJws(token).getBody();
    	} catch (Exception e) {
    		claims = null;
		}
        return claims;
    }



}