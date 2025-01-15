package com.goblin.mianshigo.aop;

import com.goblin.mianshigo.annotation.HotKey;
import com.goblin.mianshigo.common.BaseResponse;
import com.goblin.mianshigo.common.ResultUtils;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 权限校验 AOP
 *
 */
@Aspect
@Component
public class HostKeyCheckAspect {

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param hotKeyCheck
     * @return
     */
    @Around("@annotation(hotKeyCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, HotKey hotKeyCheck) throws Throwable {
        String key = hotKeyCheck.key();
        boolean prefix = hotKeyCheck.prefix();
        BaseResponse result = null ;
        if(prefix){ // 开启前缀 ， 则获取id
            Object[] args = joinPoint.getArgs();
            if(args == null || args.length == 0){
                // 执行方法
                return joinPoint.proceed();
            }
            // TODO 约定将请求类型放在第一个参数
            Object entity = args[0];
            Class<?> clazz = entity.getClass();
            if (clazz == long.class || clazz == Long.class) {
                key += entity;
            }else{
                Method getId = null;
                try {
                    getId = clazz.getDeclaredMethod("getId");
                } catch (Exception e) {
                    throw new RuntimeException("系统错误，接口方法参数顺序错误");
                }
                long id = (long) getId.invoke(entity);
                key += id;
            }
        }
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        // 获取目标方法
        Method method = methodSignature.getMethod();
        // 获取泛型
        Class<?> typeClass = getTypeClass(method);
        // 如果是热 key
        if (JdHotKeyStore.isHotKey(key)) {
            // 从本地缓存中获取缓存值
            Object cachedQuestionBankVO = JdHotKeyStore.get(key);
            if (cachedQuestionBankVO != null) {
                // 如果缓存中有值，直接返回缓存的值
                return processCachedValue(cachedQuestionBankVO, typeClass);
            }
        }
        // 如果没有热key，执行接口方法，从数据库取
        result = (BaseResponse)joinPoint.proceed();
        // 设置本地缓存
        JdHotKeyStore.smartSet(key, result.getData());
        return result;
    }

    /**
     * 处理缓存值的返回类型转换
     * @param cachedValue
     * @param typeClass
     * @param <T>
     * @return
     */
    private static <T> BaseResponse<T> processCachedValue(Object cachedValue, Class<T> typeClass) {
        if(typeClass != null){
            cachedValue = typeClass.cast(cachedValue); // 安全转换
        }
        return ResultUtils.success((T)cachedValue);
    }


    /**
     * 从方法获取其返回类型的泛型
     * @param method
     * @return
     */
    private Class<?> getTypeClass(Method method){
        // 获取方法的返回类型（包含泛型信息）
        Type genericReturnType = method.getGenericReturnType();

        // TODO 判断返回类型是否是参数化类型（带泛型） 无需判断，约定必定带泛型
        //
        ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;

//        // 获取原始类型
//        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
//        System.out.println("原始类型：" + rawType.getName());

        // 获取泛型参数
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        Class<?> typeClass = (Class<?>) typeArguments[0];
        return typeClass;
    }
}

