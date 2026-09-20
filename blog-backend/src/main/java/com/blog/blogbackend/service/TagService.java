package com.blog.blogbackend.service;

import com.blog.blogbackend.entity.Tag;
import com.blog.blogbackend.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagMapper tagMapper;
    //列表
    public List<Tag> list() {
        return tagMapper.selectList(null);
    }
    //添加
    public void add(Tag tag) {
        tagMapper.insert(tag);
    }
    //删除
    public void delete(Long id) {
        tagMapper.deleteById(id);
    }
}
