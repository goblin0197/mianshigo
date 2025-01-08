package com.goblin.mianshigo.job.cycle;

import cn.hutool.core.collection.CollUtil;
import com.goblin.mianshigo.annotation.DistributedLock;
import com.goblin.mianshigo.esdao.QuestionEsDao;
import com.goblin.mianshigo.mapper.QuestionMapper;
import com.goblin.mianshigo.model.dto.question.QuestionEsDTO;
import com.goblin.mianshigo.model.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// todo 取消注释开启任务
@Component
@Slf4j
public class IncSyncQuestionToEs {
    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionEsDao questionEsDao;
//
//    @Resource
//    private RestHighLevelClient restHighLevelClient;
    /**
     * 每分钟执行一次
     */
    @DistributedLock(key = "IncSyncQuestionToEs", leaseTime = 20000, waitTime = 5000) // 分布式锁
    @Scheduled(fixedRate = 1000 * 60)
    public void run() throws IOException {
//        try{
//            boolean ping = restHighLevelClient.ping(RequestOptions.DEFAULT);
//            if (ping) {
//                System.out.println("Elasticsearch initialized successfully.");
//            } else {
//                throw new RuntimeException("Elasticsearch ping failed1.");
//            }
//        }catch (Exception e){
//            log.error("Elasticsearch ping failed2.");
//            return ;
//        }

        // 查询近五分钟的数据
        long FIVE_MINUTES = 5 * 60 * 1000;
        Date fiveMinutesAgeDate = new Date(new Date().getTime() - FIVE_MINUTES);
        List<Question> questionList = questionMapper.listQuestionWithDelete(fiveMinutesAgeDate);
        if(CollUtil.isEmpty(questionList)){
            log.info("no inc data");
            return;
        }
        List<QuestionEsDTO> questionEsDTOList = questionList.stream().map(QuestionEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("IncSyncQuestionToEs start, total {}", total);
        for(int i = 0 ; i < total ; i += pageSize){
            int end = Math.min(total, i + pageSize);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));
        }
        log.info("IncSyncQuestionToEs end, total {}", total);
    }
}
