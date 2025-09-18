import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => (
    <nav className="navbar">
        <div className="navbar-container">
            <Link to="/" className="navbar-brand">WorkWise</Link>
            <div className="navbar-links">
                <Link to="/#features" className="nav-link">Features</Link>
                <Link to="/dashboard/find-jobs" className="nav-link">Find Jobs</Link>
                <Link to="/login" className="nav-button-secondary">Login</Link>
                <Link to="/register" className="nav-button-primary">Register</Link>
            </div>
        </div>
    </nav>
);

export default Navbar;