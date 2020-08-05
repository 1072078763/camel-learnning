package com.example.camel.component.ekp.token;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.TimeZone;

public class LtpaTokenGenerator {

    private static final byte[] HEADER = new byte[] { 0, 1, 2, 3 };

    private byte[] securityKey;

    public String getSecurityKey() {
        return new String(new BASE64Encoder().encode(securityKey));
    }

    public void setSecurityKey(String securityKey) {
        try {
            this.securityKey = new BASE64Decoder().decodeBuffer(securityKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LtpaTokenGenerator(String securityKey){
        this.setSecurityKey(securityKey);
    }

    public String getLtpaTokenByLoginName(String username){
        try {
            // token格式：4个头字符+8个创建时间字符+8个过期时间字符+若干个用户名字符+20个验证码
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT0"));
            long createTime = cal.getTimeInMillis() / 1000;
            //12小时过期
            long expireTime = createTime + (12 * 3600);
            byte[] create = Long.toHexString(createTime).toUpperCase()
                    .getBytes();
            byte[] expire = Long.toHexString(expireTime).toUpperCase()
                    .getBytes();
            // byte[] user =
            // ((this.getUserKey()==null?"":this.getUserKey())+"="+username+"/cn=users/dc=xiamenAir/dc=com").getBytes();
            byte[] user = username.getBytes("UTF-8");

            byte[] bytes = new byte[20 + user.length];
            System.arraycopy(HEADER, 0, bytes, 0, 4);
            System.arraycopy(create, 0, bytes, 4, 8);
            System.arraycopy(expire, 0, bytes, 12, 8);
            System.arraycopy(user, 0, bytes, 20, user.length);

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(bytes);

            byte[] digest = md.digest(securityKey);

            byte[] token = new byte[bytes.length + digest.length];
            System.arraycopy(bytes, 0, token, 0, bytes.length);
            System.arraycopy(digest, 0, token, bytes.length, digest.length);
            String tokenString = new String(new BASE64Encoder().encode(token));
            return tokenString;
        }catch (Exception e) {
            //System.out.println("加密Token信息发生错误：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        LtpaTokenGenerator ltpaTokenGenerator=new LtpaTokenGenerator("THJwwiVdcWKiDGW//eIUQJlth58=");
        String token=ltpaTokenGenerator.getLtpaTokenByLoginName("admin");
        System.out.println(token);
    }
}
