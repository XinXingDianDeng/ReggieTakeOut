package com.yzx.reggie.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

public class JWTUtil {
    //签名
    private static final String SIGN = "!@YANG";

    public static String getToken(Map<String, String> claim) {
        //过期时间
        LocalDateTime expireTime = LocalDateTime.now().plus(7, ChronoUnit.DAYS);

        JWTCreator.Builder builder = JWT.create();
        //设置payload
        claim.forEach(builder::withClaim);
        //设置expire time
        builder.withExpiresAt(Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant()));
        //设置sign
        String token = builder.sign(Algorithm.HMAC256(SIGN));
        return token;
    }

    public static DecodedJWT getVerify(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        } catch (JWTVerificationException jwte) {
            jwte.printStackTrace();
            return null;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
