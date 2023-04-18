package com.aurora.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.aurora.JwtUtil;
import com.aurora.utils.AuroraResult;
import com.aurora.utils.AuroraUtil;
import com.aurora.utils.CommonConst;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
@Aspect
@Component
@Slf4j
public class LoginCheckAspect {
    @Around("@annotation(loginCheck)")
    public Object around(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        if(AuroraUtil.getRequest().getHeader(CommonConst.TOKEN_HEADER)==null){
            return AuroraResult.fail("未检测到用户,请登录！");
        }
        Claims token =  JwtUtil.parseJWT(AuroraUtil.getRequest().getHeader(CommonConst.TOKEN_HEADER));
        if(token.get("uid")==null){
            return AuroraResult.fail("用户身份异常！请检查是否过期！");
        }
        if((Integer) token.get("permission") < loginCheck.value()){
            return AuroraResult.fail("权限不足！");
        }
        return joinPoint.proceed();
    }
}
