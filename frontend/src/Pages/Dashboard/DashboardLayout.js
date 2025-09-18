import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const DashboardLayout = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

     const isHirer = user?.userType === 'HIRER' || user?.userType === 'BOTH';
     const isWorker = user?.userType === 'WORKER' || user?.userType === 'BOTH';

      return (
             <div className="dashboard-layout">
                 <aside className="sidebar">
                     <div className="sidebar-header">
                         <NavLink to="/" className="sidebar-brand">WorkWise</NavLink>
                     </div>
                     <nav className="sidebar-nav">
                         <NavLink to="/dashboard/home" className="sidebar-link">Dashboard</NavLink>
                         {isWorker && <NavLink to="/dashboard/find-jobs" className="sidebar-link">Find Jobs</NavLink>}
                         {isHirer && <NavLink to="/dashboard/post-job" className="sidebar-link">Post a Job</NavLink>}
                         <NavLink to="/dashboard/my-jobs" className="sidebar-link">My Jobs</NavLink>
                         <NavLink to="/dashboard/profile" className="sidebar-link">My Profile</NavLink>
                     </nav>
                     <div className="sidebar-footer">
                         <div className="user-info">
                             <span className="user-name">{user?.name}</span>
                             <span className="user-role">{user?.userType.replace('_', ' ')}</span>
                         </div>
                         <button onClick={handleLogout} className="sidebar-logout-btn">Logout</button>
                     </div>
                 </aside>
                 <main className="dashboard-main-content">
                     <Outlet /> {/* Child routes will render here */}
                 </main>
             </div>
         );
     };

     export default DashboardLayout;