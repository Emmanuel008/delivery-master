import React from 'react';

const ParcelRequestList = ({ requests, onMarkAsRead }) => {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const getStatusBadge = (isRead) => {
    return (
      <span className={`status-badge ${isRead ? 'status-read' : 'status-unread'}`}>
        {isRead ? 'Read' : 'Unread'}
      </span>
    );
  };

  return (
    <div>
      <h3>Parcel Requests</h3>
      {requests.length === 0 ? (
        <p>No parcel requests found.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Location</th>
              <th>Price</th>
              <th>Can Deliver</th>
              <th>Phone</th>
              <th>Status</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {requests.map(request => (
              <tr key={request.id}>
                <td>{request.name}</td>
                <td>{request.location}</td>
                <td>${request.price.toFixed(2)}</td>
                <td>{request.canDeliver ? 'Yes' : 'No'}</td>
                <td>{request.phoneNumber || 'N/A'}</td>
                <td>{getStatusBadge(request.isRead)}</td>
                <td>{formatDate(request.createdAt)}</td>
                <td>
                  {!request.isRead && (
                    <button
                      className="btn btn-success"
                      onClick={() => onMarkAsRead(request.id)}
                      style={{ padding: '4px 8px', fontSize: '12px' }}
                    >
                      Mark as Read
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ParcelRequestList;
