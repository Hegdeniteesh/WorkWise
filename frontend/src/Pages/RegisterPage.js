import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Register from '../components/Auth/Register';

const RegisterPage = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleRegisterSuccess = (userData) => {
        // The api service automatically logs the user in and stores the token
        // We update the auth context and redirect
        const fullUserData = { ...userData, token: localStorage.getItem('workwise_token') };
        login(fullUserData);
        navigate('/dashboard');
    };

    return (
        <div className="auth-page">
            {/* The Register component is  existing form from src/components/Auth/Register.js */}
            <Register onRegister={handleRegisterSuccess} switchToLogin={() => navigate('/login')} />
        </div>
    );
};

export default RegisterPage;