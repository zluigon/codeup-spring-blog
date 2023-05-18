package com.codeup.codeupspringblog.controllers;

import com.codeup.codeupspringblog.models.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
public class PostController {

    @GetMapping("/posts")
    public String indexPage(Model model) {
        ArrayList<Post> posts = new ArrayList<>();

        Post postOne = new Post("First Title", "First Body");
        Post postTwo = new Post("Second Title", "Second Body");

        posts.add(postOne);
        posts.add(postTwo);

        model.addAttribute("posts", posts);

        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String id(@PathVariable int id, Model model) {
        Post post = new Post("My Title", "My Description");

        model.addAttribute("post", post);

        return "posts/show";
    }

    @GetMapping("/posts/create")
    @ResponseBody
    public String createGet() {
        return "view the form for creating a post";
    }

    @PostMapping("/posts/create")
    @ResponseBody
    public String createPost() {
        return "create a new post";
    }
}
