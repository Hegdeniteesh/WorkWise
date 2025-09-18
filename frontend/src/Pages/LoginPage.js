import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Login from '../components/Auth/Login';

const LoginPage = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleLoginSuccess = (userData) => {
        // The token is already stored in localStorage by the api service
        // We just need to update the auth context and redirect
        const fullUserData = { ...userData, token: localStorage.getItem('workwise_token') };
        login(fullUserData);
        navigate('/dashboard');
    };

    return (
        <div className="auth-page">
            {/* The Login component is existing form from src/components/Auth/Login.js */}
            <Login onLogin={handleLoginSuccess} switchToRegister={() => navigate('/register')} />
        </div>
    );
};

export default LoginPage;