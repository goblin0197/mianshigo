package com.goblin.mianshigo.blackfilter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.WordTree;
import cn.hutool.poi.word.WordUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

@Slf4j
public class BlackIpUtils {

    // 使用布隆过滤器
    public static BitMapBloomFilter bitMapBloomFilter;

    // 判断 ip 是否在黑名单中
    public static boolean isBlackIp(String ip) {
        return bitMapBloomFilter.contains(ip);
    }

    /**
     * 重建 IP 黑名单
     * @param configInfo
     */
    public static void rebuildBlackIp(String configInfo) {
        if(StrUtil.isBlank(configInfo)){
            configInfo = "{}";
        }
        // 解析yaml文件
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(configInfo, Map.class);
        // 获取黑名单
        List<String> blackIpList = (List<String>)map.get("blackIpList");
        // 加锁 防止并发冲突
        synchronized(BlackIpUtils.class){
            if(CollUtil.isNotEmpty(blackIpList)){
                // 如果ip不为空
                BitMapBloomFilter bloomFilter = new BitMapBloomFilter(958506); // 注意参数
                for (String blackIp : blackIpList) {
                    bloomFilter.add(blackIp);
                }
                bitMapBloomFilter = bloomFilter;
            }else {
                bitMapBloomFilter = new BitMapBloomFilter(100);
            }
        }
    }

}