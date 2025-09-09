import React, { useState, useEffect } from 'react';
import { userAPI } from '../../services/api';

const UserProfile = ({ user, onLogout }) => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const response = await userAPI.getStats();
      setStats(response.data);
    } catch (error) {
      console.error('Error fetching stats:', error);
    } finally {
      setLoading(false);
    }
  };

  const getUserTypeIcon = (type) => {
    switch (type) {
      case 'WORKER': return '👷';
      case 'HIRER': return '🏢';
      case 'BOTH': return '🤝';
      default: return '👤';
    }
  };

  const getLanguageDisplay = (lang) => {
    const languages = {
      'ENGLISH': 'English',
      'HINDI': 'हिंदी (Hindi)',
      'TAMIL': 'தமிழ் (Tamil)',
      'TELUGU': 'తెలుగు (Telugu)',
      'BENGALI': 'বাংলা (Bengali)',
      'MARATHI': 'मराठी (Marathi)'
    };
    return languages[lang] || lang;
  };

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <div className="welcome-section">
          <h1>
            {getUserTypeIcon(user.userType)} Welcome back, {user.name}!
          </h1>
          <p>Ready to connect with opportunities on WorkWise</p>
        </div>
        <button onClick={onLogout} className="btn-secondary">
          Logout
        </button>
      </header>

      <div className="dashboard-content">
        <div className="profile-card">
          <h2>Your Profile</h2>
          <div className="profile-info">
            <div className="info-item">
              <span className="label">Email:</span>
              <span className="value">{user.email}</span>
            </div>

            <div className="info-item">
              <span className="label">User Type:</span>
              <span className="value">
                {getUserTypeIcon(user.userType)} {user.userType}
              </span>
            </div>

            {user.city && (
              <div className="info-item">
                <span className="label">Location:</span>
                <span className="value">📍 {user.city}</span>
              </div>
            )}

            <div className="info-item">
              <span className="label">Trust Score:</span>
              <span className="value trust-score">
                ⭐ {user.trustScore || 0}/5.0
              </span>
            </div>

            <div className="info-item">
              <span className="label">Verification Status:</span>
              <span className={`value ${user.isVerified ? 'verified' : 'unverified'}`}>
                {user.isVerified ? '✅ Verified' : '❌ Not Verified'}
              </span>
            </div>

            <div className="info-item">
              <span className="label">Preferred Language:</span>
              <span className="value">
                {getLanguageDisplay(user.preferredLanguage || 'ENGLISH')}
              </span>
            </div>
          </div>
        </div>

        {stats && (
          <div className="stats-card">
            <h2>Platform Statistics</h2>
            <div className="stats-grid">
              <div className="stat-item">
                <div className="stat-number">{stats.totalUsers}</div>
                <div className="stat-label">Total Users</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">{stats.totalWorkers}</div>
                <div className="stat-label">Workers</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">{stats.totalHirers}</div>
                <div className="stat-label">Hirers</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">{stats.seasonalWorkers}</div>
                <div className="stat-label">Seasonal Workers</div>
              </div>
            </div>
          </div>
        )}

        <div className="quick-actions">
          <h2>Quick Actions</h2>
          <div className="action-buttons">
            {(user.userType === 'WORKER' || user.userType === 'BOTH') && (
              <button className="action-btn">
                🔍 Find Jobs Near Me
              </button>
            )}

            {(user.userType === 'HIRER' || user.userType === 'BOTH') && (
              <button className="action-btn">
                📝 Post a Job
              </button>
            )}

            <button className="action-btn">
              👥 Browse Workers
            </button>

            <button className="action-btn">
              ⚙️ Edit Profile
            </button>
          </div>
        </div>

        <div className="development-status">
          <h3>🚧 Development Status</h3>
          <div className="status-list">
            <div className="status-item completed">✅ User Authentication</div>
            <div className="status-item completed">✅ User Registration</div>
            <div className="status-item completed">✅ Database Models</div>
            <div className="status-item completed">✅ JWT Security</div>
            <div className="status-item in-progress">🔄 Job Management (Day 4)</div>
            <div className="status-item pending">⏳ AI Matching (Day 5)</div>
            <div className="status-item pending">⏳ Multi-language UI (Day 6)</div>
            <div className="status-item pending">⏳ Payment Integration (Day 7)</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserProfile;
