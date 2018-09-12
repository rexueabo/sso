package com.huimin.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTUtil {

	private static final String SECERT = "com.humin100";//签名字符串
    /**
     * 创建jwt
     * @param id
     * @param subject
     * @param ttlMillis 过期的时间长度
     * @return
     * @throws Exception
     */
    public static  String sign(String id, String subject, long ttlMillis)throws Exception {
			SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
			long nowMillis = System.currentTimeMillis();//生成JWT的时间
			Date now = new Date(nowMillis);
			Map<String,Object> claims = new HashMap<String,Object>();//创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
			claims.put("uid", "DSSFAWDWADAS...");
			claims.put("user_name", "admin");
			claims.put("nick_name","DASDA121");
			//下面就是在为payload添加各种标准声明和私有声明了
			JwtBuilder builder = Jwts.builder() 
					.setClaims(claims)          
			        .setId(id)                 
			        .setIssuedAt(now)           
			        .setSubject(subject)        
			        .signWith(signatureAlgorithm, SECERT);
			if (ttlMillis >= 0) {
			    long expMillis = nowMillis + ttlMillis;
			    Date exp = new Date(expMillis);
			    builder.setExpiration(exp);     //设置过期时间
			}
			return builder.compact();
    }
    
    /**
     * 解密jwt
     * @param jwt
     * @return
     * @throws Exception
     */
    public static  Claims unsign(String jwt) throws Exception{
			Claims claims = Jwts.parser()  //得到DefaultJwtParser
			   .setSigningKey(SECERT)         //设置签名的秘钥
			   .parseClaimsJws(jwt).getBody();//设置需要解析的jwt
			return claims;
    }
    
    public static void main(String[] args) throws Exception {
		String sign = sign("111", "hhhh", 10000L);
		System.out.println(sign);
//		try {
//			Thread.currentThread().sleep(15);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		System.out.println(unsign(sign));
	}
}
