package com.blog.blogbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.blogbackend.common.Result;
import com.blog.blogbackend.entity.ArticleTag;
import com.blog.blogbackend.entity.Tag;
import com.blog.blogbackend.mapper.ArticleTagMapper;
import com.blog.blogbackend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;
    @Autowired private ArticleTagMapper articleTagMapper;
    @GetMapping
    public Result<List<Tag>> list() {
        return Result.success(tagService.list());
    }
    @PostMapping
    public Result<?> add(@RequestBody Tag tag) {
        tagService.add(tag);
        return Result.success(null);
    }
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tagService.delete(id);
        articleTagMapper.delete(
                new QueryWrapper<ArticleTag>().eq("tag_id", id)
        );
        return Result.success(null);
    }
}
