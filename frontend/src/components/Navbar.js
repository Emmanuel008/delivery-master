import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Navbar = () => {
  const location = useLocation();

  return (
    <div className="navbar">
      <div className="container">
        <h1>Parcel Delivery System</h1>
        <nav>
          <Link 
            to="/" 
            className={location.pathname === '/' ? 'active' : ''}
          >
            Submit Request
          </Link>
          <Link 
            to="/admin" 
            className={location.pathname === '/admin' ? 'active' : ''}
          >
            Admin Dashboard
          </Link>
        </nav>
      </div>
    </div>
  );
};

export default Navbar;
