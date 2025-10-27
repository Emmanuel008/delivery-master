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
        <h2>Parcel Delivery Request</h2>
        <p>Please fill out the form below to submit your parcel delivery request.</p>
        
        {message && (
          <div className={`notification ${message.includes('Error') ? 'error' : 'success'}`}>
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Full Name *</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              placeholder="Enter your full name"
            />
          </div>

          <div className="form-group">
            <label htmlFor="location">Delivery Location *</label>
            <input
              type="text"
              id="location"
              name="location"
              value={formData.location}
              onChange={handleChange}
              required
              placeholder="Enter delivery address"
            />
          </div>

          <div className="form-group">
            <label htmlFor="price">Price (USD) *</label>
            <input
              type="number"
              id="price"
              name="price"
              value={formData.price}
              onChange={handleChange}
              required
              min="0"
              step="0.01"
              placeholder="Enter price"
            />
          </div>

          <div className="form-group">
            <label htmlFor="phoneNumber">Phone Number</label>
            <input
              type="tel"
              id="phoneNumber"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              placeholder="Enter your phone number (optional)"
            />
          </div>

          <div className="form-group">
            <div className="checkbox-group">
              <input
                type="checkbox"
                id="canDeliver"
                name="canDeliver"
                checked={formData.canDeliver}
                onChange={handleChange}
                required
              />
              <label htmlFor="canDeliver">
                Can we deliver the parcel to you? *
              </label>
            </div>
          </div>

          <button 
            type="submit" 
            className="btn"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Submitting...' : 'Submit Request'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default UserForm;
