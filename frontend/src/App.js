import React, { useState, useEffect } from 'react';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import UserProfile from './components/Dashboard/UserProfile';
import { healthAPI } from './services/api';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [currentView, setCurrentView] = useState('login'); // 'login' or 'register'
  const [loading, setLoading] = useState(true);
  const [backendStatus, setBackendStatus] = useState(null);

  useEffect(() => {
    // Check if user is already logged in
    const savedUser = localStorage.getItem('workwise_user');
    const savedToken = localStorage.getItem('workwise_token');

    if (savedUser && savedToken) {
      setUser(JSON.parse(savedUser));
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
  };

  const handleRegister = (userData) => {
    setUser(userData);
  };

  const handleLogout = () => {
    localStorage.removeItem('workwise_token');
    localStorage.removeItem('workwise_user');
    setUser(null);
  };

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loading-spinner"></div>
        <p>Loading WorkWise...</p>
      </div>
    );
  }

  // If user is logged in, show dashboard
  if (user) {
    return <UserProfile user={user} onLogout={handleLogout} />;
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
