import React from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Layout/Navbar';
import Footer from '../components/Layout/Footer';

const HomePage = () => {
    return (
        <div className="homepage">
            <Navbar />
            <main>
                <section className="hero-section">
                    <div className="hero-content">
                        <h1 className="hero-title">Find Work. Hire Talent. Instantly.</h1>
                        <p className="hero-subtitle">
                            WorkWise is India's most trusted platform connecting skilled labor with immediate job opportunities in your area.
                        </p>
                        <div className="hero-cta-buttons">
                            <Link to="/register?type=worker" className="btn-primary-large">I'm Looking for Work</Link>
                            <Link to="/register?type=hirer" className="btn-secondary-large">I'm Hiring Workers</Link>
                        </div>
                    </div>
                </section>

                <section id="features" className="features-section">
                    <h2 className="section-title">Why Choose WorkWise?</h2>
                    <div className="features-grid">
                        <div className="feature-card">
                            <div className="feature-icon">📍</div>
                            <h3>Hyperlocal Matching</h3>
                            <p>Find jobs and workers right in your neighborhood using precise GPS-based matching.</p>
                        </div>
                        <div className="feature-card">
                            <div className="feature-icon">🛠️</div>
                            <h3>All Skills Welcome</h3>
                            <p>From agriculture and construction to domestic help and event management, every skill has a place here.</p>
                        </div>
                        <div className="feature-card">
                            <div className="feature-icon">🔒</div>
                            <h3>Trust & Safety</h3>
                            <p>Verified profiles, transparent ratings, and a community trust score system ensure reliability.</p>
                        </div>
                        <div className="feature-card">
                            <div className="feature-icon">🤖</div>
                            <h3>AI-Powered Recommendations</h3>
                            <p>Our smart algorithm suggests the best jobs for workers and the most qualified workers for hirers.</p>
                        </div>
                    </div>
                </section>
            </main>
            <Footer />
        </div>
    );
};

export default HomePage;