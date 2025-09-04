package com.zekademirli.hostify.services;

import com.zekademirli.hostify.dto.response.AddPostResponse;
import com.zekademirli.hostify.dto.response.PostResponse;
import com.zekademirli.hostify.dto.response.UpdatePostResponse;
import com.zekademirli.hostify.entities.Post;
import com.zekademirli.hostify.entities.Property;
import com.zekademirli.hostify.entities.User;
import com.zekademirli.hostify.exceptions.ResourceNotFoundException;
import com.zekademirli.hostify.exceptions.UnauthorizedException;
import com.zekademirli.hostify.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    public final PostRepository postRepository;
    public final UserService userService;
    public final PropertyService propertyService;

    public PostService(PostRepository postRepository, UserService userService, PropertyService propertyService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @Transactional
    public AddPostResponse addPost(Long userId, Long propertyId) {
        log.info("Adding new post for owner ID: {}", userId);

        User user = userService.getOneUser(userId);
        Property property = propertyService.getOneProperty(propertyId);

        Post post = new Post();
        post.setUser(user);
        post.setProperty(property);
        Post savedPost = postRepository.save(post);

        log.info("Post saved successfully with id: {}", savedPost.getId());

        return AddPostResponse.builder()
                .id(savedPost.getId())
                .userId(user.getId())
                .propertyId(property.getId())
                .build();
    }

    @Transactional
    public UpdatePostResponse updatePost(Long userId, Long postId, Long propertyId) {
        log.info("Updating postId: {} for owner ID: {}", postId, userId);

        Post post = getOnePost(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("This post is not yours!");
        }

        Property newProperty = propertyService.getOneProperty(propertyId);
        post.setProperty(newProperty);
        Post savedPost = postRepository.save(post);
        log.info("Post updated successfully with id: {}", savedPost.getId());

        return UpdatePostResponse.builder()
                .id(savedPost.getId())
                .userId(savedPost.getUser().getId())
                .propertyId(savedPost.getProperty().getId())
                .build();
    }

    @Transactional(readOnly = true)
    public PostResponse getOnePostById(Long postId) {

        Post post = getOnePost(postId);
        return new PostResponse(
                post.getId(),
                post.getUser().getUsername(),
                post.getProperty().getName(),
                post.getProperty().getId()
        );
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream().map(post -> {
            String username = post.getUser().getUsername();
            String propertyName = post.getProperty().getName();

            return new PostResponse(
                    post.getId(),
                    username,
                    propertyName,
                    post.getProperty().getId()
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsByUserId(Long userId) {
        User user = userService.getOneUser(userId);
        List<Post> postList = postRepository.findAllByUserId(userId);

        return postList.stream()
                .map(post -> new PostResponse(
                                post.getId(),
                                user.getUsername(),
                                post.getProperty().getName(),
                                post.getProperty().getId()
                        )
                ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsByPropertyId(Long propertyId) {

        Property property = propertyService.getOneProperty(propertyId);

        List<Post> postList = postRepository.findAllByPropertyId(propertyId);
        String propertyName = property.getName();

        return postList.stream().map(post -> {
            String username = property.getOwner().getUsername();

            return new PostResponse(
                    post.getId(),
                    username,
                    propertyName,
                    propertyId
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        log.info("Deleting postId: {} for owner ID: {}", postId, userId);

        User user = userService.getOneUser(userId);
        Post post = getOnePost(postId);

        if (!post.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("This post is not yours! U can't delete this post!");
        }

        postRepository.delete(post);
        log.info("Post deleted successfully with id: {}", postId);
    }

    @Transactional
    public Post getOnePost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found ID: " + id));
    }
}