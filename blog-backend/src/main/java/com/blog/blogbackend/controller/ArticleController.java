package com.blog.blogbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.blogbackend.common.Result;
import com.blog.blogbackend.dto.ArticleDTO;
import com.blog.blogbackend.entity.Article;
import com.blog.blogbackend.mapper.ArticleMapper;
import com.blog.blogbackend.service.ArticleService;
import com.blog.blogbackend.vo.ArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ArticleMapper articleMapper;
    // 分页列表
    @GetMapping
    public Page<Article> page(int page, int size) {
        String key = "article:list:" + page + ":" + size;

        // 1. 先查 Redis
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return (Page<Article>) cached;
        }

        // 2. Redis 没有，查数据库
        Page<Article> p = new Page<>(page, size);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByDesc("create_time");
        Page<Article> result = articleMapper.selectPage(p, wrapper);

        // 3. 存 Redis，设过期时间
        redisTemplate.opsForValue().set(key, result, 10, TimeUnit.MINUTES);

        return result;
    }


    // 详情
    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        return Result.success(articleService.detail(id));
    }

    // 新增
    @PostMapping
    public Result<?> add(@RequestBody ArticleDTO articledto) {
        articleService.add(articledto);
        return Result.success(null);
    }

    // 修改
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ArticleDTO articledto) {
        articledto.setId(id);
        articleService.update(articledto);
        return Result.success(null);
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success(null);
    }

    @GetMapping("/view")
    public Result<Integer> getViewCount(@RequestParam String articlePath) {
        return Result.success(articleService.getViewCountByPath(articlePath));
    }

    @PostMapping("/view")
    public Result<?> addViewCount(@RequestParam String articlePath) {
        articleService.addViewCountByPath(articlePath);
        return Result.success(null);
    }
}
