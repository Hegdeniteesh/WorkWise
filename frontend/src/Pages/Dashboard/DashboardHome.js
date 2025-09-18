import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { Link } from 'react-router-dom';

const DashboardHome = () => {
    const { user } = useAuth();

    const getUserTypeIcon = (type) => {
        switch (type) {
            case 'WORKER': return '👷';
            case 'HIRER': return '🏢';
            case 'BOTH': return '🤝';
            default: return '👤';
        }
    };

    return (
        <div className="dashboard-page">
            <header className="page-header">
                <h1>Welcome back, {user?.name}!</h1>
                <p>Here's a quick overview of your WorkWise account.</p>
            </header>

            <div className="dashboard-grid">
                <div className="dashboard-card profile-summary-card">
                    <h3>{getUserTypeIcon(user.userType)} {user.userType.replace('_', ' ')} Profile</h3>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Location:</strong> {user.city || "Not set"}</p>
                    <div className="trust-score-display">
                        <strong>Trust Score:</strong>
                        <span>⭐ {user.trustScore || 0}/5.0</span>
                    </div>
                    <Link to="/dashboard/profile" className="btn-primary">Complete Your Profile</Link>
                </div>

                <div className="dashboard-card quick-actions-card">
                    <h3>Quick Actions</h3>
                    <div className="quick-actions-buttons">
                        {(user.userType === 'WORKER' || user.userType === 'BOTH') && (
                            <Link to="/dashboard/find-jobs" className="action-btn">🔍 Find Jobs Near Me</Link>
                        )}
                        {(user.userType === 'HIRER' || user.userType === 'BOTH') && (
                            <Link to="/dashboard/post-job" className="action-btn">📝 Post a New Job</Link>
                        )}
                         <Link to="/dashboard/my-jobs" className="action-btn">💼 View My Jobs</Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DashboardHome;