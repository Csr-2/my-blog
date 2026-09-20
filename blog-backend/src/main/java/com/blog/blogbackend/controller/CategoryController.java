package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.Result;
import com.blog.blogbackend.entity.Category;
import com.blog.blogbackend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }
    @PostMapping
    public Result<?> add(Category category) {
        categoryService.add(category);
        return Result.success(null);
    }
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success(null);
    }
    @PostMapping("/update")
    public Result<?> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.update(category);
        return Result.success(null);
    }
}
