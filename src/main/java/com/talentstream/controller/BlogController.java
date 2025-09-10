package com.talentstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.entity.Blog;
import com.talentstream.service.BlogService;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

   //get all active blogs 
    @GetMapping("/active")
    public ResponseEntity<?> getActiveBlogs() {
        return blogService.getActiveBlogs();
    }
    
    //reset all to activity status to false
    @GetMapping("/all")
    public ResponseEntity<?> getAllBlogs() {
        return blogService.getAllBlogs();
    }

    // get all inactive blogs
    @GetMapping("/inactive")
    public ResponseEntity<?> getInActiveBlogs() {
        return blogService.getInActiveBlogs();
    }

    @PutMapping("/updateOrDelete")
    public ResponseEntity<String> updateOrDeleteBlog(@RequestBody Blog blogRequest) {
        if (blogRequest.isActive()) {
            return blogService.updateBlog(blogRequest.getId(), blogRequest.getAuthor(), blogRequest.isActive());
        } else {
            return blogService.deleteBlog(blogRequest.getId());
        }
    }


}
