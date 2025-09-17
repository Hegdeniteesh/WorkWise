import React, { useState } from 'react';
import { ratingAPI } from '../../services/api';

const RatingModal = ({ job, ratedUser, onClose, onRatingSubmitted }) => {
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [review, setReview] = useState('');
  const [detailedRatings, setDetailedRatings] = useState({
    workQuality: 0,
    communication: 0,
    punctuality: 0,
    reliability: 0
  });
  const [wouldRecommend, setWouldRecommend] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (rating === 0) {
      alert('Please provide an overall rating');
      return;
    }

    setLoading(true);
    try {
      const ratingData = {
        jobId: job.id,
        ratedUserId: ratedUser.id,
        rating,
        review: review.trim() || null,
        workQuality: detailedRatings.workQuality || null,
        communication: detailedRatings.communication || null,
        punctuality: detailedRatings.punctuality || null,
        reliability: detailedRatings.reliability || null,
        wouldRecommend
      };

      const response = await ratingAPI.submit(ratingData);
      onRatingSubmitted(response.data);
      onClose();
    } catch (error) {
      alert(error.response?.data?.error || 'Failed to submit rating');
    } finally {
      setLoading(false);
    }
  };

  const StarRating = ({ value, onChange, onHover, size = 'large' }) => {
    return (
      <div className={`star-rating ${size}`}>
        {[1, 2, 3, 4, 5].map((star) => (
          <button
            key={star}
            type="button"
            className={`star ${star <= (onHover ? hoverRating : value) ? 'filled' : ''}`}
            onClick={() => onChange(star)}
            onMouseEnter={() => onHover && setHoverRating(star)}
            onMouseLeave={() => onHover && setHoverRating(0)}
          >
            ⭐
          </button>
        ))}
      </div>
    );
  };

  return (
    <div className="modal-overlay">
      <div className="rating-modal">
        <div className="modal-header">
          <h2>Rate Your Experience</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="rating-content">
          <div className="job-info">
            <h3>{job.title}</h3>
            <p>Rating: {ratedUser.name}</p>
          </div>

          <form onSubmit={handleSubmit} className="rating-form">
            <div className="overall-rating">
              <label>Overall Rating *</label>
              <StarRating
                value={rating}
                onChange={setRating}
                onHover={true}
              />
              <p className="rating-text">
                {rating > 0 && getRatingText(rating)}
              </p>
            </div>

            <div className="detailed-ratings">
              <h4>Detailed Ratings (Optional)</h4>

              <div className="rating-row">
                <label>Work Quality</label>
                <StarRating
                  value={detailedRatings.workQuality}
                  onChange={(value) => setDetailedRatings({...detailedRatings, workQuality: value})}
                  size="small"
                />
              </div>

              <div className="rating-row">
                <label>Communication</label>
                <StarRating
                  value={detailedRatings.communication}
                  onChange={(value) => setDetailedRatings({...detailedRatings, communication: value})}
                  size="small"
                />
              </div>

              <div className="rating-row">
                <label>Punctuality</label>
                <StarRating
                  value={detailedRatings.punctuality}
                  onChange={(value) => setDetailedRatings({...detailedRatings, punctuality: value})}
                  size="small"
                />
              </div>

              <div className="rating-row">
                <label>Reliability</label>
                <StarRating
                  value={detailedRatings.reliability}
                  onChange={(value) => setDetailedRatings({...detailedRatings, reliability: value})}
                  size="small"
                />
              </div>
            </div>

            <div className="review-section">
              <label htmlFor="review">Write a Review (Optional)</label>
              <textarea
                id="review"
                value={review}
                onChange={(e) => setReview(e.target.value)}
                placeholder="Share your experience working with this person..."
                rows="4"
                maxLength="500"
              />
              <div className="char-count">{review.length}/500</div>
            </div>

            <div className="recommendation-section">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  checked={wouldRecommend}
                  onChange={(e) => setWouldRecommend(e.target.checked)}
                />
                I would recommend this person to others
              </label>
            </div>

            <div className="modal-actions">
              <button type="button" className="btn-secondary" onClick={onClose}>
                Cancel
              </button>
              <button type="submit" className="btn-primary" disabled={loading || rating === 0}>
                {loading ? 'Submitting...' : 'Submit Rating'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );

  function getRatingText(rating) {
    const texts = {
      1: "Poor - Needs significant improvement",
      2: "Fair - Below expectations",
      3: "Good - Meets expectations",
      4: "Very Good - Exceeds expectations",
      5: "Excellent - Outstanding work!"
    };
    return texts[rating];
  }
};

export default RatingModal;
