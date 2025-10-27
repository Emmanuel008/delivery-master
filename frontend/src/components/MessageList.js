import React from 'react';

const MessageList = ({ messages }) => {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const getStatusBadge = (status) => {
    let badgeClass = 'status-unread';
    let statusText = status || 'Pending';
    
    if (status === 'Delivered') {
      badgeClass = 'status-read';
    } else if (status === 'Failed' || status === 'Error') {
      badgeClass = 'status-unread';
      statusText = status;
    } else {
      statusText = 'Pending';
    }
    
    return (
      <span className={`status-badge ${badgeClass}`}>
        {statusText}
      </span>
    );
  };

  return (
    <div>
      <h3>Message History</h3>
      {messages.length === 0 ? (
        <p>No messages found.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Phone Number</th>
              <th>Content</th>
              <th>Status</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            {messages.map(msg => (
              <tr key={msg.id}>
                <td>{msg.phoneNumber}</td>
                <td style={{ maxWidth: '300px', wordWrap: 'break-word' }}>
                  {msg.message}
                </td>
                <td>{getStatusBadge(msg.status)}</td>
                <td>{formatDate(msg.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default MessageList;
