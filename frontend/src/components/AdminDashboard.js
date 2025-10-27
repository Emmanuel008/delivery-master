import React, { useState, useEffect } from 'react';
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
  const [activeTab, setActiveTab] = useState('requests');

  useEffect(() => {
    fetchData();
    setupWebSocket();
  }, []);

  const fetchData = async () => {
    try {
      const [requestsResponse, messagesResponse, unreadResponse] = await Promise.all([
        axios.get('/api/parcel-requests'),
        axios.get('/api/messages'),
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
      <div className="card">
        <h2>Admin Dashboard</h2>
        <p>Manage parcel delivery requests and send messages to customers.</p>
        
        {unreadCount > 0 && (
          <div className="notification">
            You have {unreadCount} unread parcel request{unreadCount > 1 ? 's' : ''}
          </div>
        )}

        <div style={{ marginBottom: '20px' }}>
          <button
            className={`btn ${activeTab === 'requests' ? 'btn-success' : 'btn-secondary'}`}
            onClick={() => setActiveTab('requests')}
            style={{ marginRight: '10px' }}
          >
            Parcel Requests ({parcelRequests.length})
          </button>
          <button
            className={`btn ${activeTab === 'messages' ? 'btn-success' : 'btn-secondary'}`}
            onClick={() => setActiveTab('messages')}
            style={{ marginRight: '10px' }}
          >
            Messages ({messages.length})
          </button>
          <button
            className={`btn ${activeTab === 'compose' ? 'btn-success' : 'btn-secondary'}`}
            onClick={() => setActiveTab('compose')}
          >
            Compose Message
          </button>
        </div>

        {activeTab === 'requests' && (
          <ParcelRequestList
            requests={parcelRequests}
            onMarkAsRead={handleMarkAsRead}
          />
        )}

        {activeTab === 'messages' && (
          <MessageList messages={messages} />
        )}

        {activeTab === 'compose' && (
          <MessageComposer onMessageSent={handleMessageSent} />
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;
