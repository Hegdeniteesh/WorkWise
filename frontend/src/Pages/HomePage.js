import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

// Import Pages
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardLayout from './pages/Dashboard/DashboardLayout';
import DashboardHome from './pages/Dashboard/DashboardHome';
import JobSearchPage from './pages/Dashboard/JobSearchPage';
import PostJobPage from './pages/Dashboard/PostJobPage';
import ProfilePage from './pages/Dashboard/ProfilePage';
import MyJobsPage from './pages/Dashboard/MyJobsPage';

import './App.css';

// Protected Route Component
const ProtectedRoute = () => {
    const { user, loading } = useAuth();

    if (loading) {
        return (
            <div className="loading-screen">
                <div className="loading-spinner"></div>
                <p>Loading WorkWise...</p>
            </div>
        );
    }

    return user ? <Outlet /> : <Navigate to="/login" />;
};

function App() {
    const { user } = useAuth();

    return (
        <Router>
            <Routes>
                {/* Public Routes */}
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={user ? <Navigate to="/dashboard" /> : <LoginPage />} />
                <Route path="/register" element={user ? <Navigate to="/dashboard" /> : <RegisterPage />} />

                {/* Protected Dashboard Routes */}
                <Route element={<ProtectedRoute />}>
                    <Route path="/dashboard" element={<DashboardLayout />}>
                        <Route index element={<Navigate to="home" replace />} />
                        <Route path="home" element={<DashboardHome />} />
                        <Route path="find-jobs" element={<JobSearchPage />} />
                        <Route path="post-job" element={<PostJobPage />} />
                        <Route path="my-jobs" element={<MyJobsPage />} />
                        <Route path="profile" element={<ProfilePage />} />
                    </Route>
                </Route>

                 {/* Fallback Route */}
                <Route path="*" element={<Navigate to="/" />} />
            </Routes>
        </Router>
    );
}

export default App;