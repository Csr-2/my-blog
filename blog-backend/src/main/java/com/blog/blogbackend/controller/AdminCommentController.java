package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.Result;
import com.blog.blogbackend.entity.Comment;
import com.blog.blogbackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public Result<List<Comment>> list() {
        return Result.success(commentService.adminList());
    }

    @PutMapping("/{id}")
    public Result<?> audit(@PathVariable Long id, @RequestParam Integer status) {
        commentService.audit(id, status);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.success(null);
    }
}
