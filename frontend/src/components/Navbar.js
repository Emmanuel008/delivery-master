import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Navbar = ({ activeTab, onTabChange, messagesCount, unreadCount, onLogout }) => {
  const location = useLocation();
  const isAdminPage = location.pathname === '/admin';

  return (
    <div className="navbar">
      <div className="container">
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <img src="/images/logo.png" alt="NILETEE Logo" style={{ width: '40px', height: '40px', objectFit: 'contain' }} />
          <h1>NILETEE</h1>
        </div>
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
          {isAdminPage && (
            <>
              <button
                className={`nav-link ${activeTab === 'messages' ? 'active' : ''}`}
                onClick={() => onTabChange('messages')}
              >
                Messages {messagesCount > 0 && <span className="nav-badge">{messagesCount}</span>}
              </button>
              <button
                className={`nav-link ${activeTab === 'requests' ? 'active' : ''}`}
                onClick={() => onTabChange('requests')}
              >
                Parcel Requests {unreadCount > 0 && <span className="nav-badge unread">{unreadCount}</span>}
              </button>
              <button
                className={`nav-link ${activeTab === 'compose' ? 'active' : ''}`}
                onClick={() => onTabChange('compose')}
              >
                Compose Message
              </button>
            </>
          )}
          {isAdminPage && onLogout && (
            <button
              className="nav-link logout-nav-btn"
              onClick={onLogout}
            >
              Logout
            </button>
          )}
        </nav>
      </div>
    </div>
  );
};

export default Navbar;
