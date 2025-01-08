package com.goblin.mianshigo.esdao;

import com.goblin.mianshigo.model.dto.question.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 题目 ES 操作
 */
public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDTO, Long> {

    /**
     * 根据用户id查询
     * @param userId
     * @return
     */
    List<QuestionEsDao> findByUserId(Long userId);
}