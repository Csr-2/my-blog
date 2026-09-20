package com.blog.blogbackend.controller;
import com.blog.blogbackend.common.Result;
import com.blog.blogbackend.entity.Comment;
import com.blog.blogbackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 提交评论（游客）
    @PostMapping
    public Result<?> add(@RequestBody Comment comment) {
        commentService.add(comment);
        return Result.success(null);
    }

    // 查评论（按 articlePath）
    @GetMapping
    public Result<List<Comment>> list(@RequestParam String articlePath) {
        return Result.success(commentService.listByArticleId(articlePath));
    }
}