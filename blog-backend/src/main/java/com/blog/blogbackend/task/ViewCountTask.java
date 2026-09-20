package com.blog.blogbackend.task;

import com.blog.blogbackend.entity.Article;
import com.blog.blogbackend.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
//定时任务
public class ViewCountTask {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ArticleMapper articleMapper;
    // 每 5 分钟同步一次
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncViewCount() {
        // 获取 Redis 中的 viewCount 数据
        // 更新到数据库中
        Set<String> keys = redisTemplate.keys("article:view:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long articleId = Long.valueOf(key.substring("article:view:".length()));
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) continue;

            int viewCount = Integer.parseInt(value.toString());

            Article article = new Article();
            article.setId(articleId);
            article.setViewCount(viewCount);
            articleMapper.updateById(article);
        }
    }
}
