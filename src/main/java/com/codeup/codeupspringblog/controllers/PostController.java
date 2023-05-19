package com.codeup.codeupspringblog.controllers;

import com.codeup.codeupspringblog.models.Post;
import com.codeup.codeupspringblog.repositories.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private final PostRepository postDao;

    public PostController(PostRepository postDao) {
        this.postDao = postDao;
    }

    @GetMapping("/posts")
    public String indexPage(Model model) {
        List<Post> posts = postDao.findAll();

        model.addAttribute("posts", posts);

        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String id(@PathVariable Long id, Model model) {
        Post post = postDao.getReferenceById(id);

        model.addAttribute("post", post);

        return "posts/show";
    }

    @GetMapping("/posts/create")
    public String createGet() {
        return "posts/create";
    }

    @PostMapping("/posts/create")
    public String createPost(@RequestParam String title, @RequestParam String body) {
        Post post = new Post(title, body);
        postDao.save(post);
        return "redirect:/posts";
    }

    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam Long id){
        postDao.deleteById(id);
        return "redirect:/posts";
    }
}
