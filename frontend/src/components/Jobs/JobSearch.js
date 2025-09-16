import React, { useState, useEffect } from 'react';
import { jobAPI } from '../../services/api';

const JobSearch = ({ onJobSelect }) => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [searchFilters, setSearchFilters] = useState({
    skill: '',
    city: '',
    state: '',
    category: '',
    minPrice: '',
    maxPrice: '',
    isUrgent: false,
    isAgriculture: false
  });
  const [currentLocation, setCurrentLocation] = useState(null);

  const skillCategories = [
    'CONSTRUCTION', 'ELECTRICAL', 'PLUMBING', 'CARPENTRY', 'PAINTING',
    'FARMING', 'CROP_HARVESTING', 'LIVESTOCK', 'IRRIGATION',
    'CLEANING', 'COOKING', 'CHILDCARE', 'GARDENING',
    'RETAIL_ASSISTANCE', 'DELIVERY', 'PHOTOGRAPHY', 'DRIVING'
  ];

  useEffect(() => {
    fetchJobs();
  }, []);

  const fetchJobs = async () => {
    setLoading(true);
    setError('');

    try {
      const response = await jobAPI.getAll();
      setJobs(response.data.jobs || []);
    } catch (err) {
      setError('Failed to fetch jobs');
      console.error('Error fetching jobs:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    setLoading(true);
    setError('');

    try {
      // Filter out empty values
      const filters = Object.fromEntries(
        Object.entries(searchFilters).filter(([_, value]) => value !== '' && value !== false)
      );

      const response = await jobAPI.search(filters);
      setJobs(response.data.jobs || []);
    } catch (err) {
      setError('Search failed');
      console.error('Search error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setSearchFilters({
      ...searchFilters,
      [e.target.name]: value
    });
  };

  const getCurrentLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setCurrentLocation({
            lat: position.coords.latitude,
            lng: position.coords.longitude
          });
          // Auto-search nearby jobs
          searchNearbyJobs(position.coords.latitude, position.coords.longitude);
        },
        (error) => {
          console.error('Error getting location:', error);
          setError('Unable to get your location');
        }
      );
    }
  };

  const searchNearbyJobs = async (lat, lng, radius = 10) => {
    setLoading(true);
    try {
      const response = await jobAPI.getNearbyJobs(lat, lng, radius);
      setJobs(response.data);
    } catch (err) {
      setError('Failed to search nearby jobs');
    } finally {
      setLoading(false);
    }
  };

  const applyForJob = async (jobId) => {
    try {
      const response = await jobAPI.applyForJob(jobId);
      alert(response.data.message);
      fetchJobs(); // Refresh jobs list
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to apply for job');
    }
  };

  const getJobStatusColor = (status) => {
    switch (status) {
      case 'POSTED': return '#4CAF50';
      case 'WORKER_ASSIGNED': return '#ff9500';
      case 'IN_PROGRESS': return '#2196F3';
      case 'COMPLETED': return '#9C27B0';
      default: return '#666';
    }
  };

  const formatPrice = (price, paymentType) => {
    if (!price) return 'Price not specified';
    return `₹${price.toLocaleString()} ${paymentType ? `per ${paymentType.toLowerCase().replace('_', ' ')}` : ''}`;
  };

  const getTimeSincePosted = (createdAt) => {
    const now = new Date();
    const posted = new Date(createdAt);
    const diffInHours = Math.floor((now - posted) / (1000 * 60 * 60));

    if (diffInHours < 1) return 'Just posted';
    if (diffInHours < 24) return `${diffInHours} hours ago`;
    const diffInDays = Math.floor(diffInHours / 24);
    return `${diffInDays} days ago`;
  };

  return (
    <div className="job-search-container">
      <div className="search-header">
        <h2>Find Jobs</h2>
        <p>Discover opportunities near you</p>
      </div>

      {/* Search Filters */}
      <div className="search-filters">
        <div className="filter-row">
          <div className="filter-group">
            <input
              type="text"
              name="skill"
              value={searchFilters.skill}
              onChange={handleFilterChange}
              placeholder="Search by skill (e.g., farming, cleaning)"
            />
          </div>

          <div className="filter-group">
            <input
              type="text"
              name="city"
              value={searchFilters.city}
              onChange={handleFilterChange}
              placeholder="City"
            />
          </div>

          <div className="filter-group">
            <select
              name="category"
              value={searchFilters.category}
              onChange={handleFilterChange}
            >
              <option value="">All Categories</option>
              {skillCategories.map(category => (
                <option key={category} value={category}>
                  {category.replace(/_/g, ' ')}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="filter-row">
          <div className="filter-group">
            <input
              type="number"
              name="minPrice"
              value={searchFilters.minPrice}
              onChange={handleFilterChange}
              placeholder="Min Price (₹)"
            />
          </div>

          <div className="filter-group">
            <input
              type="number"
              name="maxPrice"
              value={searchFilters.maxPrice}
              onChange={handleFilterChange}
              placeholder="Max Price (₹)"
            />
          </div>

          <div className="filter-group checkbox-filters">
            <label>
              <input
                type="checkbox"
                name="isUrgent"
                checked={searchFilters.isUrgent}
                onChange={handleFilterChange}
              />
              Urgent Jobs Only
            </label>

            <label>
              <input
                type="checkbox"
                name="isAgriculture"
                checked={searchFilters.isAgriculture}
                onChange={handleFilterChange}
              />
              Agricultural Jobs
            </label>
          </div>
        </div>

        <div className="filter-actions">
          <button onClick={handleSearch} className="btn-search">
            🔍 Search Jobs
          </button>
          <button onClick={getCurrentLocation} className="btn-location">
            📍 Jobs Near Me
          </button>
          <button onClick={fetchJobs} className="btn-clear">
            Clear Filters
          </button>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      {/* Jobs List */}
      <div className="jobs-container">
        {loading ? (
          <div className="loading-message">
            <div className="loading-spinner"></div>
            <p>Searching for jobs...</p>
          </div>
        ) : jobs.length === 0 ? (
          <div className="no-jobs">
            <h3>No jobs found</h3>
            <p>Try adjusting your search filters or check back later for new opportunities.</p>
          </div>
        ) : (
          <div className="jobs-grid">
            {jobs.map(job => (
              <div key={job.id} className="job-card">
                <div className="job-header">
                  <div className="job-title-section">
                    <h3 className="job-title">
                      {job.title}
                      {job.isUrgent && <span className="urgent-badge">🚨 URGENT</span>}
                    </h3>
                    <div className="job-meta">
                      <span className="job-category">{job.skillCategory?.replace(/_/g, ' ')}</span>
                      <span
                        className="job-status"
                        style={{ color: getJobStatusColor(job.status) }}
                      >
                        {job.status?.replace(/_/g, ' ')}
                      </span>
                    </div>
                  </div>
                  <div className="job-price">
                    {formatPrice(job.offeredPrice, job.paymentType)}
                  </div>
                </div>

                <div className="job-details">
                  <div className="job-info">
                    <div className="info-item">
                      <span className="icon">🛠️</span>
                      <span>{job.skillRequired}</span>
                    </div>

                    <div className="info-item">
                      <span className="icon">📍</span>
                      <span>{job.city}, {job.state}</span>
                    </div>

                    <div className="info-item">
                      <span className="icon">👥</span>
                      <span>{job.workersNeeded} worker{job.workersNeeded > 1 ? 's' : ''} needed</span>
                    </div>

                    {job.startDate && (
                      <div className="info-item">
                        <span className="icon">📅</span>
                        <span>Starts: {new Date(job.startDate).toLocaleDateString()}</span>
                      </div>
                    )}

                    {job.estimatedHours && (
                      <div className="info-item">
                        <span className="icon">⏱️</span>
                        <span>{job.estimatedHours} hours estimated</span>
                      </div>
                    )}
                  </div>

                  {job.description && (
                    <div className="job-description">
                      <p>{job.description.length > 100
                        ? `${job.description.substring(0, 100)}...`
                        : job.description}
                      </p>
                    </div>
                  )}

                  {/* Agricultural specific info */}
                  {job.cropType && (
                    <div className="agricultural-info">
                      <span className="icon">🌾</span>
                      <span>Crop: {job.cropType}</span>
                      {job.areaSize && <span> | Area: {job.areaSize} acres</span>}
                    </div>
                  )}

                  {/* Job perks */}
                  <div className="job-perks">
                    {job.toolsProvided && <span className="perk">🔧 Tools Provided</span>}
                    {job.foodProvided && <span className="perk">🍽️ Food Provided</span>}
                    {job.accommodationProvided && <span className="perk">🏠 Stay Provided</span>}
                  </div>

                  <div className="job-footer">
                    <div className="job-timing">
                      <span className="posted-time">
                        Posted {getTimeSincePosted(job.createdAt)}
                      </span>
                    </div>

                    <div className="job-actions">
                      <button
                        onClick={() => onJobSelect && onJobSelect(job)}
                        className="btn-view"
                      >
                        View Details
                      </button>

                      {job.status === 'POSTED' && (
                        <button
                          onClick={() => applyForJob(job.id)}
                          className="btn-apply"
                        >
                          Apply Now
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Job count */}
      {!loading && jobs.length > 0 && (
        <div className="results-summary">
          <p>Showing {jobs.length} job{jobs.length !== 1 ? 's' : ''}</p>
        </div>
      )}
    </div>
  );
};

export default JobSearch;
