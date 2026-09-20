package com.blog.blogbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.blogbackend.entity.Comment;
import com.blog.blogbackend.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    //提交评论
    public void add(Comment comment){
        comment.setStatus(0);
        commentMapper.insert(comment);
    }
    //删除
    public void delete(Long id){
        commentMapper.deleteById(id);
    }
    //文章评论
    public List<Comment> listByArticleId(String articlePath){
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("article_path", articlePath)
                .eq("status", 1)
                .orderByDesc("create_time");
        return commentMapper.selectList(wrapper);
    }
    //后台评论列表
    public List<Comment> adminList(){
        return commentMapper.adminList();
    }
    //审核评论
    public void audit(Long id, Integer status){
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(status);
        commentMapper.updateById(comment);
    }
}
