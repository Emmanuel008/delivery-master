import React, { useState } from 'react';
import axios from 'axios';

const UserForm = () => {
  const [formData, setFormData] = useState({
    name: '',
    location: '',
    price: '',
    canDeliver: false,
    phoneNumber: ''
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState('');

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setMessage('');

    try {
      await axios.post('/api/parcel-requests', {
        ...formData,
        price: parseFloat(formData.price)
      });

      setMessage('Request submitted successfully! We will contact you soon.');
      setFormData({
        name: '',
        location: '',
        price: '',
        canDeliver: false,
        phoneNumber: ''
      });
    } catch (error) {
      setMessage('Error submitting request. Please try again.');
      console.error('Error:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="container">
      <div className="card">
        <div style={{ marginBottom: 12 }}>
          <h2 style={{ marginBottom: 6 }}>Parcel Delivery Request</h2>
          <p className="subtle">Fill in your details and we will reach out to confirm delivery.</p>
        </div>
        
        {message && (
          <div className={`notification ${message.includes('Error') ? 'error' : 'success'}`} style={{ marginBottom: 16 }}>
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="name" className="label-required">Full Name</label>
              <input
                className="input"
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
                placeholder="e.g. John Doe"
              />
              <div className="hint">Your legal or preferred full name.</div>
            </div>

            <div className="form-group">
              <label htmlFor="phoneNumber">Phone Number</label>
              <input
                className="input"
                type="tel"
                id="phoneNumber"
                name="phoneNumber"
                value={formData.phoneNumber}
                onChange={handleChange}
                placeholder="e.g. +255625313162"
              />
              <div className="hint">Include country code (e.g. +255).</div>
            </div>

            <div className="form-group">
              <label htmlFor="location" className="label-required">Delivery Location</label>
              <input
                className="input"
                type="text"
                id="location"
                name="location"
                value={formData.location}
                onChange={handleChange}
                required
                placeholder="Street, area or landmark"
              />
              <div className="hint">Provide enough detail for the courier to find you.</div>
            </div>

            <div className="form-group">
              <label htmlFor="price" className="label-required">Price (TZS)</label>
              <input
                className="input"
                type="number"
                id="price"
                name="price"
                value={formData.price}
                onChange={handleChange}
                required
                min="0"
                step="0.01"
                placeholder="0.00"
              />
              <div className="hint">Estimated parcel value or delivery fee.</div>
            </div>
          </div>

          <div className="checkbox-card">
            <input
              type="checkbox"
              id="canDeliver"
              name="canDeliver"
              checked={formData.canDeliver}
              onChange={handleChange}
              required
            />
            <label htmlFor="canDeliver">Can we deliver the parcel to you?</label>
          </div>

          <div style={{ display: 'flex', gap: 12, marginTop: 12 }}>
            <button 
              type="submit" 
              className="btn btn-primary btn-block"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Submitting...' : 'NILETEE'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default UserForm;
