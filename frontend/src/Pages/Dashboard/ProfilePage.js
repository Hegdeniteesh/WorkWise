import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { userAPI } from '../../services/api';

const ProfilePage = () => {
    const { user } = useAuth();
    const [formData, setFormData] = useState({
        name: user.name || '',
        phoneNumber: user.phoneNumber || '',
        address: user.address || '',
        city: user.city || '',
        state: user.state || '',
        pincode: user.pincode || '',
        bio: user.bio || '',
    });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage('');
        try {
            await userAPI.update(user.userId, formData);
            setMessage('Profile updated successfully!');
            // Note: You might want to update the user in AuthContext here as well
        } catch (error) {
            setMessage('Failed to update profile. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="dashboard-page">
            <header className="page-header">
                <h1>Your Profile</h1>
                <p>Keep your information up to date to attract more opportunities.</p>
            </header>
            <div className="profile-form-container">
                <form onSubmit={handleSubmit} className="profile-form">
                    {/* Add form fields for each item in formData state */}
                    {/* Example for 'name' field */}
                    <div className="form-group">
                        <label htmlFor="name">Full Name</label>
                        <input type="text" id="name" name="name" value={formData.name} onChange={handleChange} />
                    </div>
                    {/* ... other fields for phoneNumber, address, city, state, pincode, bio ... */}

                     <div className="form-group">
                        <label htmlFor="bio">About You (Bio)</label>
                        <textarea id="bio" name="bio" rows="4" value={formData.bio} onChange={handleChange} placeholder="Tell us about your skills and experience..."></textarea>
                    </div>

                    <button type="submit" className="btn-primary" disabled={loading}>
                        {loading ? 'Saving...' : 'Save Changes'}
                    </button>
                    {message && <p className="form-message">{message}</p>}
                </form>
            </div>
        </div>
    );
};

export default ProfilePage;