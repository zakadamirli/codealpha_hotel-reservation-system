package com.zekademirli.hostify.services;

import com.zekademirli.hostify.dto.request.AddCommentRequest;
import com.zekademirli.hostify.dto.response.AddCommentResponse;
import com.zekademirli.hostify.dto.response.CommentResponse;
import com.zekademirli.hostify.dto.response.UserCommentResponse;
import com.zekademirli.hostify.entities.Comment;
import com.zekademirli.hostify.entities.Post;
import com.zekademirli.hostify.entities.Property;
import com.zekademirli.hostify.entities.User;
import com.zekademirli.hostify.exceptions.ResourceNotFoundException;
import com.zekademirli.hostify.repository.CommentRepository;
import com.zekademirli.hostify.repository.PostRepository;
import com.zekademirli.hostify.repository.PropertyRepository;
import com.zekademirli.hostify.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PropertyRepository propertyRepository;


    public CommentService(PropertyRepository propertyRepository, ModelMapper modelMapper, CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public AddCommentResponse addComment(AddCommentRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setUser(user);
        comment.setPost(post);
        comment.setProperty(property);

        Comment savedComment = commentRepository.save(comment);

        return modelMapper.map(savedComment, AddCommentResponse.class);
    }

    @Transactional
    public List<CommentResponse> getAllComments() {
        List<Comment> comments = commentRepository.findAll();

        return comments.stream()
                .map(comment -> {

                    String username = comment.getUser().getUsername();

                    return new CommentResponse(
                            comment.getId(),
                            comment.getContent(),
                            comment.getPost().getId(),
                            username,
                            comment.getCreatedAt()
                    );
                }).collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse getOneComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found {}", commentId);
                    return new ResourceNotFoundException("Comment not found");
                });
        String username = comment.getUser().getUsername();
        if (username != null) {
            log.info("Found comment {}", commentId);
        }
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId(),
                username,
                comment.getCreatedAt()
        );
    }

    @Transactional
    public void deleteComment(Long commentId) {
        log.warn("Deleting comment {}", commentId);
        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

            commentRepository.delete(comment);
            log.info("Comment deleted {}", commentId);
        } catch (ResourceNotFoundException e) {
            log.error("Error occurred while deleting comment with ID: {}", commentId);
        }
    }

    @Transactional
    public List<CommentResponse> getAllCommentsByPostId(Long postId) {
        List<Comment> commentList = commentRepository.findAllByPostId(postId);

        return commentList.stream().map(comment -> {
            String username = comment.getUser().getUsername();
            return new CommentResponse(
                    comment.getId(),
                    comment.getContent(),
                    comment.getPost().getId(),
                    username,
                    comment.getCreatedAt()
            );
        }).toList();
    }

    @Transactional
    public List<UserCommentResponse> getAllCommentsByUserId(Long userId) {

        List<Comment> comments = commentRepository.findAllByUserId(userId);
        return comments.stream()
                .map(comment -> new UserCommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getPost().getId()
                ))
                .collect(Collectors.toList());
    }
}