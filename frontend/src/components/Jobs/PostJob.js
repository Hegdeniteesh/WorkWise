import React, { useState } from 'react';
import { jobAPI } from '../../services/api';

const PostJob = ({ onJobPosted, onCancel }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    skillRequired: '',
    skillCategory: '',
    offeredPrice: '',
    paymentType: 'HOURLY',
    startDate: '',
    endDate: '',
    estimatedHours: '',
    latitude: '',
    longitude: '',
    address: '',
    city: '',
    state: '',
    pincode: '',
    workersNeeded: 1,
    requirements: '',
    toolsProvided: false,
    accommodationProvided: false,
    foodProvided: false,
    isUrgent: false,
    // Agricultural specific fields
    cropType: '',
    areaSize: '',
    weatherDependency: false
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [currentLocation, setCurrentLocation] = useState(null);

  const skillCategories = [
    'CONSTRUCTION', 'ELECTRICAL', 'PLUMBING', 'CARPENTRY', 'PAINTING', 'MASONRY',
    'FARMING', 'CROP_HARVESTING', 'LIVESTOCK', 'IRRIGATION', 'AGRICULTURAL_MACHINERY',
    'CLEANING', 'COOKING', 'CHILDCARE', 'ELDERCARE', 'GARDENING',
    'RETAIL_ASSISTANCE', 'INVENTORY_MANAGEMENT', 'CASHIER', 'DELIVERY',
    'PHOTOGRAPHY', 'VIDEOGRAPHY', 'GRAPHIC_DESIGN', 'EVENT_DECORATION',
    'DRIVING', 'LOGISTICS', 'MOVING_SERVICES',
    'GENERAL_LABOR', 'SECURITY', 'MAINTENANCE', 'OTHER'
  ];

  const paymentTypes = [
    { value: 'HOURLY', label: 'Per Hour' },
    { value: 'DAILY', label: 'Per Day' },
    { value: 'FIXED', label: 'Fixed Amount' },
    { value: 'PER_ACRE', label: 'Per Acre (Agricultural)' },
    { value: 'PER_PIECE', label: 'Per Piece/Unit' },
    { value: 'WEEKLY', label: 'Per Week' },
    { value: 'MONTHLY', label: 'Per Month' }
  ];

  const getCurrentLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setFormData({
            ...formData,
            latitude: position.coords.latitude.toString(),
            longitude: position.coords.longitude.toString()
          });
          setCurrentLocation({
            lat: position.coords.latitude,
            lng: position.coords.longitude
          });
        },
        (error) => {
          console.error('Error getting location:', error);
          setError('Unable to get your location. Please enter manually.');
        }
      );
    } else {
      setError('Geolocation is not supported by this browser.');
    }
  };

  const handleChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setFormData({
      ...formData,
      [e.target.name]: value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Validate required fields
      if (!formData.title || !formData.skillRequired || !formData.address) {
        throw new Error('Please fill in all required fields');
      }

      if (!formData.latitude || !formData.longitude) {
        throw new Error('Location is required. Please use "Get My Location" or enter coordinates manually.');
      }

      // Convert string values to appropriate types
      const jobData = {
        ...formData,
        latitude: parseFloat(formData.latitude),
        longitude: parseFloat(formData.longitude),
        offeredPrice: formData.offeredPrice ? parseFloat(formData.offeredPrice) : null,
        estimatedHours: formData.estimatedHours ? parseInt(formData.estimatedHours) : null,
        workersNeeded: parseInt(formData.workersNeeded),
        areaSize: formData.areaSize ? parseFloat(formData.areaSize) : null,
        startDate: formData.startDate ? new Date(formData.startDate).toISOString() : null,
        endDate: formData.endDate ? new Date(formData.endDate).toISOString() : null
      };

      const response = await jobAPI.create(jobData);
      onJobPosted(response.data);
    } catch (err) {
      setError(err.response?.data?.error || err.message || 'Failed to post job');
    } finally {
      setLoading(false);
    }
  };

  const isAgriculturalJob = ['FARMING', 'CROP_HARVESTING', 'LIVESTOCK', 'IRRIGATION', 'AGRICULTURAL_MACHINERY'].includes(formData.skillCategory);

  return (
    <div className="post-job-container">
      <div className="post-job-card">
        <div className="post-job-header">
          <h2>Post a New Job</h2>
          <p>Find the right workers for your needs</p>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit} className="post-job-form">
          {/* Basic Job Information */}
          <div className="form-section">
            <h3>Job Details</h3>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="title">Job Title *</label>
                <input
                  type="text"
                  id="title"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  required
                  placeholder="e.g., Farm Helper Needed, House Cleaning, Construction Work"
                />
              </div>

              <div className="form-group">
                <label htmlFor="skillCategory">Job Category *</label>
                <select
                  id="skillCategory"
                  name="skillCategory"
                  value={formData.skillCategory}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select a category</option>
                  {skillCategories.map(category => (
                    <option key={category} value={category}>
                      {category.replace(/_/g, ' ')}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="skillRequired">Skills Required *</label>
              <input
                type="text"
                id="skillRequired"
                name="skillRequired"
                value={formData.skillRequired}
                onChange={handleChange}
                required
                placeholder="e.g., Wheat Harvesting, Electrical Wiring, House Cleaning"
              />
            </div>

            <div className="form-group">
              <label htmlFor="description">Job Description</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows="4"
                placeholder="Describe the work to be done, requirements, and any special instructions..."
              />
            </div>
          </div>

          {/* Pricing and Payment */}
          <div className="form-section">
            <h3>Pricing & Payment</h3>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="paymentType">Payment Type *</label>
                <select
                  id="paymentType"
                  name="paymentType"
                  value={formData.paymentType}
                  onChange={handleChange}
                  required
                >
                  {paymentTypes.map(type => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="offeredPrice">
                  Offered Price (INR) {formData.paymentType !== 'FIXED' && `per ${formData.paymentType.toLowerCase().replace('_', ' ')}`}
                </label>
                <input
                  type="number"
                  id="offeredPrice"
                  name="offeredPrice"
                  value={formData.offeredPrice}
                  onChange={handleChange}
                  min="0"
                  step="0.01"
                  placeholder="Enter amount"
                />
              </div>
            </div>
          </div>

          {/* Timing and Workers */}
          <div className="form-section">
            <h3>Schedule & Requirements</h3>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="startDate">Start Date</label>
                <input
                  type="datetime-local"
                  id="startDate"
                  name="startDate"
                  value={formData.startDate}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label htmlFor="endDate">End Date</label>
                <input
                  type="datetime-local"
                  id="endDate"
                  name="endDate"
                  value={formData.endDate}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="workersNeeded">Workers Needed *</label>
                <input
                  type="number"
                  id="workersNeeded"
                  name="workersNeeded"
                  value={formData.workersNeeded}
                  onChange={handleChange}
                  min="1"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="estimatedHours">Estimated Hours</label>
                <input
                  type="number"
                  id="estimatedHours"
                  name="estimatedHours"
                  value={formData.estimatedHours}
                  onChange={handleChange}
                  min="1"
                  placeholder="Total hours of work"
                />
              </div>
            </div>
          </div>

          {/* Location */}
          <div className="form-section">
            <h3>Location</h3>

            <div className="location-input">
              <button
                type="button"
                onClick={getCurrentLocation}
                className="btn-location"
              >
                📍 Get My Location
              </button>
              {currentLocation && (
                <span className="location-status">✅ Location captured</span>
              )}
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="latitude">Latitude *</label>
                <input
                  type="number"
                  id="latitude"
                  name="latitude"
                  value={formData.latitude}
                  onChange={handleChange}
                  step="any"
                  required
                  placeholder="e.g., 28.6139"
                />
              </div>

              <div className="form-group">
                <label htmlFor="longitude">Longitude *</label>
                <input
                  type="number"
                  id="longitude"
                  name="longitude"
                  value={formData.longitude}
                  onChange={handleChange}
                  step="any"
                  required
                  placeholder="e.g., 77.2090"
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="address">Full Address *</label>
              <textarea
                id="address"
                name="address"
                value={formData.address}
                onChange={handleChange}
                required
                rows="3"
                placeholder="Enter complete address including landmarks"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="city">City</label>
                <input
                  type="text"
                  id="city"
                  name="city"
                  value={formData.city}
                  onChange={handleChange}
                  placeholder="City"
                />
              </div>

              <div className="form-group">
                <label htmlFor="state">State</label>
                <input
                  type="text"
                  id="state"
                  name="state"
                  value={formData.state}
                  onChange={handleChange}
                  placeholder="State"
                />
              </div>

              <div className="form-group">
                <label htmlFor="pincode">PIN Code</label>
                <input
                  type="text"
                  id="pincode"
                  name="pincode"
                  value={formData.pincode}
                  onChange={handleChange}
                  placeholder="PIN Code"
                  pattern="[0-9]{6}"
                />
              </div>
            </div>
          </div>

          {/* Agricultural Specific Fields */}
          {isAgriculturalJob && (
            <div className="form-section agricultural-section">
              <h3>🌾 Agricultural Work Details</h3>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="cropType">Crop Type</label>
                  <input
                    type="text"
                    id="cropType"
                    name="cropType"
                    value={formData.cropType}
                    onChange={handleChange}
                    placeholder="e.g., Wheat, Rice, Cotton, Sugarcane"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="areaSize">Area Size (Acres)</label>
                  <input
                    type="number"
                    id="areaSize"
                    name="areaSize"
                    value={formData.areaSize}
                    onChange={handleChange}
                    min="0"
                    step="0.1"
                    placeholder="e.g., 5.5"
                  />
                </div>
              </div>

              <div className="form-group checkbox-group">
                <label>
                  <input
                    type="checkbox"
                    name="weatherDependency"
                    checked={formData.weatherDependency}
                    onChange={handleChange}
                  />
                  Work is weather dependent
                </label>
              </div>
            </div>
          )}

          {/* Additional Options */}
          <div className="form-section">
            <h3>Additional Information</h3>

            <div className="form-group">
              <label htmlFor="requirements">Special Requirements</label>
              <textarea
                id="requirements"
                name="requirements"
                value={formData.requirements}
                onChange={handleChange}
                rows="3"
                placeholder="Any special tools, experience, or requirements needed..."
              />
            </div>

            <div className="checkbox-grid">
              <label className="checkbox-item">
                <input
                  type="checkbox"
                  name="toolsProvided"
                  checked={formData.toolsProvided}
                  onChange={handleChange}
                />
                Tools/Equipment Provided
              </label>

              <label className="checkbox-item">
                <input
                  type="checkbox"
                  name="accommodationProvided"
                  checked={formData.accommodationProvided}
                  onChange={handleChange}
                />
                Accommodation Provided
              </label>

              <label className="checkbox-item">
                <input
                  type="checkbox"
                  name="foodProvided"
                  checked={formData.foodProvided}
                  onChange={handleChange}
                />
                Food/Meals Provided
              </label>

              <label className="checkbox-item urgent">
                <input
                  type="checkbox"
                  name="isUrgent"
                  checked={formData.isUrgent}
                  onChange={handleChange}
                />
                🚨 Urgent Job
              </label>
            </div>
          </div>

          {/* Form Actions */}
          <div className="form-actions">
            <button
              type="button"
              onClick={onCancel}
              className="btn-secondary"
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn-primary"
              disabled={loading}
            >
              {loading ? 'Posting Job...' : 'Post Job'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PostJob;
