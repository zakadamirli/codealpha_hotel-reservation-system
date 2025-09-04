package com.zekademirli.hostify.services;

import com.zekademirli.hostify.dto.request.UserRequest;
import com.zekademirli.hostify.dto.response.UserResponse;
import com.zekademirli.hostify.entities.User;
import com.zekademirli.hostify.exceptions.ResourceNotFoundException;
import com.zekademirli.hostify.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Transactional
    public ResponseEntity<UserResponse> resisterUser(UserRequest userRequest) {

        if (!userRepository.existsByEmail(userRequest.getEmail())) {
            User user = modelMapper.map(userRequest, User.class);
            User savedUser = userRepository.save(user);
            UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @Transactional
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserResponse.class))
                .orElseThrow(() -> new ResourceNotFoundException("User not found ID: " + id));
    }

    @Transactional
    public User getOneUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found ID: " + id));
    }

    @Transactional
    public UserResponse updateUser(UserRequest userRequest, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found ID: " + id));
        modelMapper.map(userRequest, user);
        userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {} ", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            userRepository.delete(user);
            log.info("Deleted user with id: {} ", id);
        } catch (ResourceNotFoundException e) {
            log.error("Error occurred while deleting user with ID: {} ", id, e);
        }
    }
}