package com.rbdip.bookstore.review;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PurchaseLookup purchaseLookup;

    public ReviewService(ReviewRepository reviewRepository, PurchaseLookup purchaseLookup) {
        this.reviewRepository = reviewRepository;
        this.purchaseLookup = purchaseLookup;
    }

    public Review addReview(Long productId, String authorName, Integer rating, String comment) {
        purchaseLookup.hasAnyOrdersAndItems();
        Review review = new Review(productId, authorName == null ? "anonymous" : authorName, rating, comment);
        return reviewRepository.save(review);
    }

    public List<Review> listReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}
