package com.workwise.service;

import com.workwise.model.Rating;
import com.workwise.repository.JobRepository;
import com.workwise.repository.RatingRepository;
import com.workwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    public List<Rating> getUserReviews(Long userId) {
        return ratingRepository.findByRatedUserId(userId);
    }
}