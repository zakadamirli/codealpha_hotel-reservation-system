package com.zekademirli.hostify.controller;

import com.zekademirli.hostify.dto.response.AddPostResponse;
import com.zekademirli.hostify.dto.response.PostResponse;
import com.zekademirli.hostify.dto.response.UpdatePostResponse;
import com.zekademirli.hostify.services.PostService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @PostMapping
    public AddPostResponse addPost(@RequestParam @NotNull @Positive Long userId,
                                   @RequestParam @NotNull @Positive Long propertyId) {

        return postService.addPost(userId, propertyId);
    }

    @PutMapping("/{postId}")
    public UpdatePostResponse updatePost(@RequestParam @NotNull @Positive Long userId,
                                         @PathVariable @NotNull @Positive Long postId,
                                         @RequestParam @NotNull @Positive Long propertyId) {
        return postService.updatePost(userId, postId, propertyId);
    }

    @GetMapping("/{postId}")
    public PostResponse getPostById(@PathVariable @NotNull @Positive Long postId) {
        return postService.getOnePostById(postId);
    }

    @GetMapping
    public List<PostResponse> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/get-all-posts-by-user-id")
    public List<PostResponse> getAllPostsByUserId(@RequestParam @NotNull @Positive Long userId) {
        return postService.getAllPostsByUserId(userId);
    }

    @GetMapping("/get-all-posts-by-property-id")
    public List<PostResponse> getAllPostsByPropertyId(@RequestParam @NotNull @Positive Long propertyId) {
        return postService.getAllPostsByPropertyId(propertyId);
    }

    @DeleteMapping
    public void deletePostById(@RequestParam @NotNull @Positive Long userId,
                               @RequestParam @NotNull @Positive Long postId) {
        postService.deletePost(userId, postId);
    }
}