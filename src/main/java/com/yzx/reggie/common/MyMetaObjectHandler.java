package com.yzx.reggie.common;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.yzx.reggie.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        String token = request.getHeader("Authorization");
        String user = null;
        if (token != null) {
            DecodedJWT verify = JWTUtil.getVerify(token);
            user = verify.getClaim("userId").asString();
        }
        Long emp = (Long) request.getSession().getAttribute("employee");

        if (emp != null) {
            metaObject.setValue("createUser", emp);
            metaObject.setValue("updateUser", emp);
        }
        if (user != null) {
            metaObject.setValue("createUser", Long.valueOf(user));
            metaObject.setValue("updateUser", Long.valueOf(user));
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        String user = null;
        if (token != null) {
            DecodedJWT verify = JWTUtil.getVerify(token);
            user = verify.getClaim("userId").asString();

        }
        Long emp = (Long) request.getSession().getAttribute("employee");
        metaObject.setValue("updateTime", LocalDateTime.now());
        if (emp != null) {
            metaObject.setValue("updateUser", emp);
        }
        if (user != null) {
            metaObject.setValue("updateUser", Long.valueOf(user));
        }
    }
}
