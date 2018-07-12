package com.kd.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <b>Application name:</b> ContextHolderUtils.java <br>
 * <b>Application describing:上下文工具类 </b> <br>
 * <b>Copyright:</b> Copyright &copy; 2015 zhursh 版权所有。<br>
 * <b>Company:</b> zhursh <br>
 * <b>Date:</b> 2015-7-6 <br>
 * @author <a href="mailto:zhursh133@sina.com"> zhursh </a>
 * @version V1.0
 */
public class ContextHolderUtils {
    /**
     * SpringMvc下获取request
     * @return
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    /**
     * SpringMvc下获取session
     * @return
     */
    public static HttpSession getSession() {
        HttpSession session = getRequest().getSession();
        return session;
    }
}
