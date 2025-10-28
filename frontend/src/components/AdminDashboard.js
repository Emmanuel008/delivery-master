import React, { useState, useEffect, useMemo } from 'react';
import axios from 'axios';
import ParcelRequestList from './ParcelRequestList';
import MessageComposer from './MessageComposer';
import MessageList from './MessageList';
import NotificationService from '../services/NotificationService';

const AdminDashboard = () => {
  const [parcelRequests, setParcelRequests] = useState([]);
  const [messages, setMessages] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('messages');

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');

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
      <div className="container">
        <div className="card">
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="card" style={{ marginBottom: 16 }}>
        <div className="dashboard-header">
          <h2>Admin Dashboard</h2>
          <p className="subtle">Manage parcel requests and SMS notifications</p>
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
        </div>

        <div className="toolbar">
          <div className="tabs">
            <button
              className={`tab ${activeTab === 'messages' ? 'active' : ''}`}
              onClick={() => setActiveTab('messages')}
            >
              Messages ({messages.length})
            </button>
            <button
              className={`tab ${activeTab === 'requests' ? 'active' : ''}`}
              onClick={() => setActiveTab('requests')}
            >
              Parcel Requests ({parcelRequests.length})
            </button>
            <button
              className={`tab ${activeTab === 'compose' ? 'active' : ''}`}
              onClick={() => setActiveTab('compose')}
            >
              Compose Message
            </button>
          </div>
          {activeTab === 'messages' && (
            <div className="filters">
              <input
                className="input"
                placeholder="Search by phone or content..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
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
          )}
        </div>
      </div>

      {activeTab === 'messages' && (
        <div className="card">
          <MessageList messages={filteredMessages} />
        </div>
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
  );
};

export default AdminDashboard;
