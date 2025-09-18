import React from 'react';
import JobSearch from '../../components/Jobs/JobSearch';

const JobSearchPage = () => {
    const handleJobSelect = (job) => {
        // modal to show job details
        console.log("Selected Job:", job);
        alert(`You selected "${job.title}"`);
    };

    return (
        <div className="dashboard-page">
             <header className="page-header">
                <h1>Find Work Opportunities</h1>
                <p>Search and filter jobs based on your skills and location.</p>
            </header>
            <JobSearch onJobSelect={handleJobSelect} />
        </div>
    );
};

export default JobSearchPage;