import React from 'react';
import PostJob from '../../components/Jobs/PostJob';
import { useNavigate } from 'react-router-dom';

const PostJobPage = () => {
    const navigate = useNavigate();

    const handleJobPosted = () => {
        alert("Job Posted Successfully!");
        navigate('/dashboard/my-jobs');
    };

    return (
        <div className="dashboard-page">
             <header className="page-header">
                <h1>Post a New Job</h1>
                <p>Fill out the details below to find the perfect worker for your needs.</p>
            </header>
            <PostJob onJobPosted={handleJobPosted} onCancel={() => navigate('/dashboard/home')} />
        </div>
    );
};

export default PostJobPage;