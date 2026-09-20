package com.blog.blogbackend.service;

import com.blog.blogbackend.entity.Category;
import com.blog.blogbackend.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    // 分类列表
    public List<Category> list() {
        return categoryMapper.selectList(null);
    }
    //新增
    public void add(Category category) {
        categoryMapper.insert(category);
    }
    //删除
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
    //修改
    public void update(Category category) {
        categoryMapper.updateById(category);
    }
}
