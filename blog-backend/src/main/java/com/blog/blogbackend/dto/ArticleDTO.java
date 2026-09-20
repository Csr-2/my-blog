package com.blog.blogbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String cover;
    private Long categoryId;
    private Long authorId;
    private Integer status;
    private List<Long> tagIds;   // 标签 id 列表
}
