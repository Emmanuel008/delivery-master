import React, { useState, useEffect, useMemo } from 'react';
import axios from 'axios';
import { HiShieldCheck, HiDocumentText, HiCube, HiPencil, HiBell, HiUser, HiMenu, HiX, HiHome } from 'react-icons/hi';
import ParcelRequestList from './ParcelRequestList';
import MessageComposer from './MessageComposer';
import MessageList from './MessageList';
import NotificationService from '../services/NotificationService';

const AdminDashboard = ({ onNavbarUpdate, activeTab: propActiveTab, onTabChange, onLogout }) => {
  const [parcelRequests, setParcelRequests] = useState([]);
  const [messages, setMessages] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const activeTab = propActiveTab || 'dashboard';

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');

  // Update navbar when data changes
  useEffect(() => {
    if (onNavbarUpdate) {
      onNavbarUpdate({
        activeTab,
        messagesCount: messages.length,
        unreadCount
      });
    }
  }, [activeTab, messages.length, unreadCount, onNavbarUpdate]);

  useEffect(() => {
    fetchData();
    setupWebSocket();
  }, []);

  const fetchData = async () => {
    try {
      const [requestsResponse, messagesResponse, unreadResponse] = await Promise.all([
        axios.get('/api/parcel-requests'),
        axios.get('/api/messages/sms'),
        axios.get('/api/parcel-requests/unread-count')
      ]);

      setParcelRequests(requestsResponse.data);
      setMessages(messagesResponse.data);
      setUnreadCount(unreadResponse.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const setupWebSocket = () => {
    NotificationService.connect((notification) => {
      if (notification.type === 'new_request') {
        setParcelRequests(prev => [notification.data, ...prev]);
        setUnreadCount(prev => prev + 1);
      }
    });
  };

  const handleMarkAsRead = async (id) => {
    try {
      await axios.put(`/api/parcel-requests/${id}/mark-read`);
      setParcelRequests(prev => 
        prev.map(req => req.id === id ? { ...req, isRead: true } : req)
      );
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Error marking as read:', error);
    }
  };

  const handleMessageSent = (newMessage) => {
    setMessages(prev => [newMessage, ...prev]);
  };

  // Metrics
  const { pendingCount, deliveredCount, failedCount } = useMemo(() => {
    const toLower = (v) => (v || '').toString().toLowerCase();
    let pending = 0, delivered = 0, failed = 0;
    for (const m of messages) {
      const s = toLower(m.status);
      if (s === 'delivered') delivered++;
      else if (s === 'failed' || s === 'error') failed++;
      else pending++;
    }
    return { pendingCount: pending, deliveredCount: delivered, failedCount: failed };
  }, [messages]);

  // Filters
  const filteredMessages = useMemo(() => {
    const q = search.trim().toLowerCase();
    return messages.filter(m => {
      const matchesQuery = !q || (m.phoneNumber?.toLowerCase().includes(q) || m.message?.toLowerCase().includes(q));
      const s = (m.status || '').toLowerCase();
      const matchesStatus = statusFilter === 'all' || s === statusFilter;
      return matchesQuery && matchesStatus;
    });
  }, [messages, search, statusFilter]);

  if (loading) {
    return (
      <div className="dashboard-wrapper">
        {sidebarOpen && <div className="sidebar-overlay" onClick={() => setSidebarOpen(false)}></div>}
        <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
          <div className="sidebar-header">
            <div className="sidebar-logo">
              <HiShieldCheck className="logo-icon" />
              <h2>Admin Panel</h2>
            </div>
            <button className="sidebar-toggle" onClick={() => setSidebarOpen(!sidebarOpen)}>
              {sidebarOpen ? <HiX /> : <HiMenu />}
            </button>
          </div>
        </aside>
        <main className="dashboard-main">
          <div className="top-header">
            <h1 className="page-title">
              {activeTab === 'dashboard' && 'Dashboard'}
              {activeTab === 'messages' && 'Messages'}
              {activeTab === 'requests' && 'Parcel Requests'}
              {activeTab === 'compose' && 'Compose Message'}
            </h1>
            <div className="header-actions">
              <button className="header-icon-btn"><HiBell /></button>
              <button className="header-icon-btn active"><HiUser /></button>
              {onLogout && (
                <button className="logout-btn" onClick={onLogout}>Logout</button>
              )}
            </div>
          </div>
          <div className="dashboard-content">
            <div className="card">
              <p>Loading...</p>
            </div>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="dashboard-wrapper">
      {sidebarOpen && <div className="sidebar-overlay" onClick={() => setSidebarOpen(false)}></div>}
      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-header">
          <div className="sidebar-logo">
            <img src="/images/logo.png" alt="Logo" className="logo-icon" />
            <h2>Admin Panel</h2>
          </div>
          <button className="sidebar-toggle" onClick={() => setSidebarOpen(!sidebarOpen)}>
            {sidebarOpen ? <HiX /> : <HiMenu />}
          </button>
        </div>
        <nav className="sidebar-nav">
          <button
            className={`nav-item ${activeTab === 'dashboard' ? 'active' : ''}`}
            onClick={() => {
              onTabChange('dashboard');
              if (window.innerWidth <= 768) setSidebarOpen(false);
            }}
          >
            <HiHome className="nav-icon" />
            <span className="nav-text">Dashboard</span>
          </button>
          <button
            className={`nav-item ${activeTab === 'messages' ? 'active' : ''}`}
            onClick={() => {
              onTabChange('messages');
              if (window.innerWidth <= 768) setSidebarOpen(false);
            }}
          >
            <HiDocumentText className="nav-icon" />
            <span className="nav-text">Messages</span>
            {messages.length > 0 && <span className="nav-badge">{messages.length}</span>}
          </button>
          <button
            className={`nav-item ${activeTab === 'requests' ? 'active' : ''}`}
            onClick={() => {
              onTabChange('requests');
              if (window.innerWidth <= 768) setSidebarOpen(false);
            }}
          >
            <HiCube className="nav-icon" />
            <span className="nav-text">Parcel Requests</span>
            {unreadCount > 0 && <span className="nav-badge unread">{unreadCount}</span>}
          </button>
          <button
            className={`nav-item ${activeTab === 'compose' ? 'active' : ''}`}
            onClick={() => {
              onTabChange('compose');
              if (window.innerWidth <= 768) setSidebarOpen(false);
            }}
          >
            <HiPencil className="nav-icon" />
            <span className="nav-text">Compose Message</span>
          </button>
        </nav>
      </aside>
      <main className="dashboard-main">
        <button 
          className="mobile-menu-btn"
          onClick={() => setSidebarOpen(true)}
        >
          <HiMenu />
        </button>
        
        <div className="top-header">
          <h1 className="page-title">
            {activeTab === 'dashboard' && 'Dashboard'}
            {activeTab === 'messages' && 'Messages'}
            {activeTab === 'requests' && 'Parcel Requests'}
            {activeTab === 'compose' && 'Compose Message'}
          </h1>
          <div className="header-actions">
            <button className="header-icon-btn"><HiBell /></button>
            <button className="header-icon-btn active"><HiUser /></button>
            {onLogout && (
              <button className="logout-btn" onClick={onLogout}>Logout</button>
            )}
          </div>
        </div>

        <div className="dashboard-content">
          {activeTab === 'dashboard' && (
            <div className="card" style={{ marginBottom: 16 }}>
              <div className="dashboard-header">
                <h2>Dashboard Overview</h2>
                <p className="subtle">Key statistics and metrics</p>
              </div>
              <div className="stats-grid">
                <div className="stat-card">
                  <div className="stat-title">Unread Requests</div>
                  <div className="stat-value">{unreadCount}</div>
                </div>
                <div className="stat-card">
                  <div className="stat-title">SMS Pending</div>
                  <div className="stat-value warning">{pendingCount}</div>
                </div>
                <div className="stat-card">
                  <div className="stat-title">SMS Delivered</div>
                  <div className="stat-value success">{deliveredCount}</div>
                </div>
                <div className="stat-card">
                  <div className="stat-title">SMS Failed</div>
                  <div className="stat-value danger">{failedCount}</div>
                </div>
                <div className="stat-card">
                  <div className="stat-title">Total Messages</div>
                  <div className="stat-value">{messages.length}</div>
                </div>
                <div className="stat-card">
                  <div className="stat-title">Total Requests</div>
                  <div className="stat-value">{parcelRequests.length}</div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'messages' && (
            <>
              <div className="card" style={{ marginBottom: 16 }}>
                <div className="filters">
                  <input
                    className="input"
                    placeholder="Search by phone or content..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    style={{ flex: 1 }}
                  />
                  <select
                    className="select"
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                  >
                    <option value="all">All statuses</option>
                    <option value="pending">Pending</option>
                    <option value="delivered">Delivered</option>
                    <option value="failed">Failed</option>
                    <option value="error">Error</option>
                  </select>
                </div>
              </div>
              <div className="card">
                <MessageList messages={filteredMessages} />
              </div>
            </>
          )}

          {activeTab === 'requests' && (
            <div className="card">
              <ParcelRequestList
                requests={parcelRequests}
                onMarkAsRead={handleMarkAsRead}
              />
            </div>
          )}

          {activeTab === 'compose' && (
            <div className="card">
              <MessageComposer onMessageSent={handleMessageSent} />
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

export default AdminDashboard;
