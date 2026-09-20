package com.blog.blogbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.blogbackend.config.RedisConfig;
import com.blog.blogbackend.dto.ArticleDTO;
import com.blog.blogbackend.entity.*;
import com.blog.blogbackend.mapper.*;
import com.blog.blogbackend.vo.ArticleVO;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
   @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired private TagMapper tagMapper;
    @Autowired private CategoryMapper categoryMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private RedisTemplate<String, Object> redisTemplate;

    // 分页列表（只查已发布）
    public Page<Article> page(int page, int size) {
        Page<Article> p = new Page<>(page, size);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByDesc("create_time");
        return articleMapper.selectPage(p, wrapper);
    }
    // ========== 详情（返回 VO） ==========
    public ArticleVO detail(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) return null;

        // 浏览量 +1（Redis 计数）
        String viewKey = "article:view:" + id;
        redisTemplate.opsForValue().increment(viewKey);

        // 从 Redis 取最新浏览量
        Object viewCount = redisTemplate.opsForValue().get(viewKey);

        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);
        vo.setViewCount(viewCount == null ? 0 : Integer.parseInt(viewCount.toString()));

        // 分类名
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) vo.setCategoryName(category.getName());
        }

        // 作者名
        if (article.getAuthorId() != null) {
            User user = userMapper.selectById(article.getAuthorId());
            if (user != null) vo.setAuthorName(user.getNickname());
        }

        // 标签
        vo.setTags(getTagsByArticleId(id));
        return vo;
    }

    // 新增
    @Transactional
    public void add(ArticleDTO dto) {
        // 1. 存文章
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);   // 复制同名字段
        articleMapper.insert(article);

        // 2. 存标签关联
        if (dto.getTagIds() != null) {
            for (Long tagId : dto.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
        redisTemplate.delete("article:list:*"); // 删除缓存
    }

    // 修改
    @Transactional
    public void update(ArticleDTO dto) {
        // 1. 改文章
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        articleMapper.updateById(article);

        // 删旧关联，插新关联
        articleTagMapper.delete(
                new QueryWrapper<ArticleTag>().eq("article_id", dto.getId())
        );
        saveArticleTags(dto.getId(), dto.getTagIds());
    }

    // ========== 删除 ==========
    @Transactional
    public void delete(Long id) {
        articleMapper.deleteById(id);
        articleTagMapper.delete(
                new QueryWrapper<ArticleTag>().eq("article_id", id)
        );
    }
    // 根据路径查文章
    public Article getByPath(String articlePath) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("article_path", articlePath);
        return articleMapper.selectOne(wrapper);
    }
    // 根据路径加文章浏览量
    public Integer addViewCountByPath(String articlePath) {
        Article article = getByPath(articlePath);
        if (article == null) return 0;

        String key = "article:view:" + article.getId();
        Long count = redisTemplate.opsForValue().increment(key);
        return count == null ? 0 : count.intValue();
    }
    // 根据路径查文章浏览量
    public Integer getViewCountByPath(String articlePath) {
        Article article = getByPath(articlePath);
        if (article == null) return 0;

        Object value = redisTemplate.opsForValue().get("article:view:" + article.getId());
        return value == null ? 0 : Integer.parseInt(value.toString());
    }
    // ========== 私有方法：保存标签关联 ==========
    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;
        for (Long tagId : tagIds) {
            ArticleTag at = new ArticleTag();
            at.setArticleId(articleId);
            at.setTagId(tagId);
            articleTagMapper.insert(at);
        }
    }

    // ========== 私有方法：查文章的标签 ==========
    private List<Tag> getTagsByArticleId(Long articleId) {
        List<ArticleTag> ats = articleTagMapper.selectList(
                new QueryWrapper<ArticleTag>().eq("article_id", articleId)
        );
        if (ats.isEmpty()) return List.of();

        List<Long> tagIds = ats.stream()
                .map(ArticleTag::getTagId)
                .toList();
        return tagMapper.selectBatchIds(tagIds);
    }
}
