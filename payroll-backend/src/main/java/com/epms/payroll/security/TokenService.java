package com.epms.payroll.security;
import org.springframework.stereotype.Service; import java.util.Map; import java.util.UUID; import java.util.concurrent.ConcurrentHashMap;
@Service public class TokenService { private final Map<String,String> tokens=new ConcurrentHashMap<>(); public String issue(String username){String t=UUID.randomUUID().toString();tokens.put(t,username);return t;} public String username(String token){return tokens.get(token);} public void revoke(String token){tokens.remove(token);} }
