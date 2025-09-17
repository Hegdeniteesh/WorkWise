package com.workwise.controller;

import com.workwise.repository.JobRepository;
import com.workwise.repository.UserRepository;
import com.workwise.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "http://localhost:3000")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

}
