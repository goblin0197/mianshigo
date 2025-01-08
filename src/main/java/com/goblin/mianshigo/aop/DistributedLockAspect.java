package com.goblin.mianshigo.aop;

import com.goblin.mianshigo.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class DistributedLockAspect {
    @Resource
    private RedissonClient redissonClient;

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint , DistributedLock distributedLock) throws Exception {
        String key = distributedLock.key();
        long leaseTime = distributedLock.leaseTime();
        long waitTime = distributedLock.waitTime();
        TimeUnit timeUnit = distributedLock.timeUnit();

        RLock lock = redissonClient.getLock(key);
        boolean acquired = false;

        try {
            acquired = lock.tryLock(waitTime,leaseTime,timeUnit);
            if (acquired) {
                // 获取锁成功，执行目标方法
                return joinPoint.proceed();
            } else {
                // 获取锁失败，抛出异常或处理逻辑
                throw new RuntimeException("Could not acquire lock: " + key);
            }
        } catch (Throwable e) {
            throw new Exception(e);
        }finally {
            if (acquired) {
                // 释放锁
                lock.unlock();
            }
        }
    }
}
