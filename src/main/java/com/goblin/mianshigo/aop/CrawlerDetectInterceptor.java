package com.goblin.mianshigo.aop;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.goblin.mianshigo.annotation.CrawlerDetect;
import com.goblin.mianshigo.common.ErrorCode;
import com.goblin.mianshigo.exception.BusinessException;
import com.goblin.mianshigo.manager.CounterManager;
import com.goblin.mianshigo.model.entity.User;
import com.goblin.mianshigo.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 反爬虫检测
 *
 */
@Aspect
@Component
public class CrawlerDetectInterceptor {

    @Resource
    private UserService userService;

    // 达到调用次数告警
    @NacosValue(value = "${ban.count:20}", autoRefreshed = true)
    private int BAN_COUNT ;

    // 达到调用次数封号
    @NacosValue(value = "${warn.count:10}", autoRefreshed = true)
    private int WARN_COUNT ;

    @Resource
    private CounterManager counterManager;

    /**
     * 执行拦截
     * 统计的次数为该用户调用需要反爬虫的任何接口总次数
     * 只对需要登录的接口有效，否则报错
     * @param joinPoint
     * @param crawlerDetect
     * @return
     */
    @Around("@annotation(crawlerDetect)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, CrawlerDetect crawlerDetect) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        // 拼接访问key
        String key = String.format("mianshigo:user:access:%s",loginUserId);
        // 获取一分钟内访问次数，180秒过期
        long count = counterManager.incrAndGetCounter(key, 1, TimeUnit.MINUTES, 180);
        // 是否封号
        if(count > BAN_COUNT){
            // 踢下线
            StpUtil.kickout(loginUserId);
            StpUtil.disable(loginUserId, 60 * 30);
            // 封号
//            User updateUser = new User();
//            updateUser.setUserRole(BAN_ROLE);
//            updateUser.setId(loginUserId);
//            userService.updateById(updateUser);
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "访问太频繁，已被封号30分钟");
        }
        // 是否告警
        if(count > WARN_COUNT){
            // 可以改为向管理员发送邮件通知
            throw new BusinessException(110, "警告访问太频繁");
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

