package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器 - C端用户版
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 2. 从请求头中获取令牌
        // 注意：前端约定的头名字通常是 "authentication" 或者 "token"，请根据你的前端代码确认
        // 苍穹外卖标准前端传的是 "authentication"
        String token = request.getHeader(jwtProperties.getUserTokenName());

        // 3. 校验令牌
        try {
            log.info("jwt校验:{}", token);
            // 解析令牌，如果解析失败会抛出异常，进入catch块
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);

            // 获取用户ID
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());

            log.info("当前用户id:{}", userId);

            // 4. 将用户ID存入ThreadLocal，方便后续Controller和Service直接获取
            BaseContext.setCurrentId(userId);

            // 5. 放行
            return true;
        } catch (Exception e) {
            // 4. 不通过，响应 401 状态码
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后，清理ThreadLocal，防止内存泄漏
        BaseContext.removeCurrentId();
    }
}