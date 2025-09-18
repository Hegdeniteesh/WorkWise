import React, { createContext, useState, useEffect, useContext } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('workwise_token');
        const savedUser = localStorage.getItem('workwise_user');
        if (token && savedUser) {
            setUser(JSON.parse(savedUser));
        }
        setLoading(false);
    }, []);

    const login = (userData) => {
        localStorage.setItem('workwise_token', userData.token);
        // Store user data without the token
        const userDetails = { ...userData };
        delete userDetails.token;
        localStorage.setItem('workwise_user', JSON.stringify(userDetails));
        setUser(userDetails);
    };

    const logout = () => {
        localStorage.removeItem('workwise_token');
        localStorage.removeItem('workwise_user');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};