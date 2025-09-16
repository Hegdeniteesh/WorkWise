import React, { useState, useEffect } from 'react';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import UserProfile from './components/Dashboard/UserProfile';
import PostJob from './components/Jobs/PostJob';
import JobSearch from './components/Jobs/JobSearch';
import { healthAPI } from './services/api';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [currentView, setCurrentView] = useState('login');
  const [activeTab, setActiveTab] = useState('dashboard');
  const [loading, setLoading] = useState(true);
  const [backendStatus, setBackendStatus] = useState(null);

  useEffect(() => {
    // Check if user is already logged in
    const savedUser = localStorage.getItem('workwise_user');
    const savedToken = localStorage.getItem('workwise_token');

    if (savedUser && savedToken) {
      setUser(JSON.parse(savedUser));
      setCurrentView('dashboard');
    }

    // Check backend connectivity
    checkBackendHealth();

    setLoading(false);
  }, []);

  const checkBackendHealth = async () => {
    try {
      const response = await healthAPI.health();
      setBackendStatus(response.data);
    } catch (error) {
      console.error('Backend connection failed:', error);
      setBackendStatus({ status: 'DOWN', error: 'Connection failed' });
    }
  };

  const handleLogin = (userData) => {
    setUser(userData);
    setCurrentView('dashboard');
    setActiveTab('dashboard');
  };

  const handleRegister = (userData) => {
    setUser(userData);
    setCurrentView('dashboard');
    setActiveTab('dashboard');
  };

  const handleLogout = () => {
    localStorage.removeItem('workwise_token');
    localStorage.removeItem('workwise_user');
    setUser(null);
    setCurrentView('login');
    setActiveTab('dashboard');
  };

  const handleJobPosted = (jobData) => {
    alert('Job posted successfully!');
    setActiveTab('jobs');
  };

  const handleJobSelect = (job) => {
    // You can implement a job detail modal or page here
    console.log('Selected job:', job);
  };

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loading-spinner"></div>
        <p>Loading WorkWise...</p>
      </div>
    );
  }

  // If user is logged in, show dashboard with tabs
  if (user) {
    return (
      <div className="app-dashboard">
        <header className="dashboard-nav">
          <div className="nav-brand">
            <h1>🚀 WorkWise</h1>
          </div>

          <nav className="nav-tabs">
            <button
              className={`nav-tab ${activeTab === 'dashboard' ? 'active' : ''}`}
              onClick={() => setActiveTab('dashboard')}
            >
              Dashboard
            </button>

            <button
              className={`nav-tab ${activeTab === 'jobs' ? 'active' : ''}`}
              onClick={() => setActiveTab('jobs')}
            >
              Find Jobs
            </button>

            {(user.userType === 'HIRER' || user.userType === 'BOTH') && (
              <button
                className={`nav-tab ${activeTab === 'post-job' ? 'active' : ''}`}
                onClick={() => setActiveTab('post-job')}
              >
                Post Job
              </button>
            )}

            <button
              className={`nav-tab ${activeTab === 'profile' ? 'active' : ''}`}
              onClick={() => setActiveTab('profile')}
            >
              Profile
            </button>
          </nav>

          <button onClick={handleLogout} className="btn-logout">
            Logout
          </button>
        </header>

        <main className="dashboard-main">
          {activeTab === 'dashboard' && (
            <UserProfile user={user} onLogout={handleLogout} />
          )}

          {activeTab === 'jobs' && (
            <JobSearch onJobSelect={handleJobSelect} />
          )}

          {activeTab === 'post-job' && (
            <PostJob
              onJobPosted={handleJobPosted}
              onCancel={() => setActiveTab('dashboard')}
            />
          )}

          {activeTab === 'profile' && (
            <div className="profile-settings">
              <h2>Profile Settings</h2>
              <p>Profile management features coming in Day 5!</p>
            </div>
          )}
        </main>
      </div>
    );
  }

  // Show authentication screens
  return (
    <div className="App">
      <div className="app-header">
        <div className="brand">
          <h1>🚀 WorkWise</h1>
          <p>India's Leading Labor Marketplace Platform</p>
        </div>

        {backendStatus && (
          <div className={`backend-status ${backendStatus.status.toLowerCase()}`}>
            <div className="status-indicator">
              {backendStatus.status === 'UP' ? '🟢' : '🔴'}
            </div>
            <div className="status-info">
              <span>Backend: {backendStatus.status}</span>
              {backendStatus.totalUsers !== undefined && (
                <small>{backendStatus.totalUsers} users registered</small>
              )}
            </div>
          </div>
        )}
      </div>

      <main className="app-main">
        {currentView === 'login' ? (
          <Login
            onLogin={handleLogin}
            switchToRegister={() => setCurrentView('register')}
          />
        ) : (
          <Register
            onRegister={handleRegister}
            switchToLogin={() => setCurrentView('login')}
          />
        )}
      </main>

      <footer className="app-footer">
        <div className="feature-highlights">
          <div className="feature">
            <span className="icon">🌾</span>
            <span>Agricultural Workers</span>
          </div>
          <div className="feature">
            <span className="icon">🏗️</span>
            <span>Construction Labor</span>
          </div>
          <div className="feature">
            <span className="icon">🏠</span>
            <span>Domestic Services</span>
          </div>
          <div className="feature">
            <span className="icon">🤖</span>
            <span>AI-Powered Matching</span>
          </div>
          <div className="feature">
            <span className="icon">🌍</span>
            <span>Multi-Language Support</span>
          </div>
        </div>

        <p>&copy; 2025 WorkWise - Connecting Workers, Creating Opportunities</p>
      </footer>
    </div>
  );
}

export default App;
