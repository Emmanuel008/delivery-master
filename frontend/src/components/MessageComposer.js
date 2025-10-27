import React, { useState } from 'react';
import axios from 'axios';

const MessageComposer = ({ onMessageSent }) => {
  const [formData, setFormData] = useState({
    content: '',
    phoneNumber: ''
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSendingSms, setIsSendingSms] = useState(false);
  const [message, setMessage] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setMessage('');

    try {
      const response = await axios.post('/api/messages', formData);
      onMessageSent(response.data);
      setMessage('Message saved successfully!');
      setFormData({
        content: '',
        phoneNumber: ''
      });
    } catch (error) {
      setMessage('Error saving message. Please try again.');
      console.error('Error:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSendSms = async () => {
    if (!formData.content || !formData.phoneNumber) {
      setMessage('Please fill in both phone number and message content');
      return;
    }

    setIsSendingSms(true);
    setMessage('');

    try {
      const response = await axios.post('/api/messages/send-sms', formData);
      
      if (response.data.statusCode === 200) {
        // Parse the body JSON string
        let body;
        try {
          body = typeof response.data.body === 'string' 
            ? JSON.parse(response.data.body) 
            : response.data.body;
        } catch (e) {
          body = response.data.body;
        }
        
        if (body && body.success) {
          setMessage(`✅ SMS sent successfully! Message ID: ${body.data?.shootId || 'N/A'}`);
        } else {
          setMessage('⚠️ SMS sent, but response indicates failure.');
        }
      } else {
        setMessage(`❌ SMS sending failed with status ${response.data.statusCode}`);
      }
    } catch (error) {
      setMessage('❌ Error sending SMS. Please check the console for details.');
      console.error('Error:', error);
    } finally {
      setIsSendingSms(false);
    }
  };

  const handleUseSample = async () => {
    try {
      const response = await axios.get('/api/messages/sample');
      setFormData({
        content: response.data.content,
        phoneNumber: response.data.phoneNumber
      });
    } catch (error) {
      console.error('Error fetching sample message:', error);
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
        <h3 style={{ margin: 0 }}>Compose Message</h3>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={handleUseSample}
          style={{ padding: '8px 16px', fontSize: '14px' }}
        >
          Use Sample Message
        </button>
      </div>
      <p style={{ marginBottom: '20px', color: '#666' }}>
        Send an SMS to a customer by entering their phone number and message content. Use the "Save Message" button to store the message, or "Send SMS" to send it immediately via SMS.
      </p>
      
      {message && (
        <div className={`notification ${message.includes('Error') || message.includes('failed') ? 'error' : 'success'}`}>
          {message}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="phoneNumber">Phone Number *</label>
          <input
            type="tel"
            id="phoneNumber"
            name="phoneNumber"
            value={formData.phoneNumber}
            onChange={handleChange}
            required
            placeholder="Enter customer's phone number"
          />
        </div>

        <div className="form-group">
          <label htmlFor="content">Message Content *</label>
          <textarea
            id="content"
            name="content"
            value={formData.content}
            onChange={handleChange}
            required
            rows="6"
            placeholder="Enter your message to the customer"
          />
        </div>

        <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={isSubmitting || isSendingSms}
            style={{ flex: 1 }}
          >
            {isSubmitting ? 'Saving...' : 'Save Message'}
          </button>
          
          <button 
            type="button"
            className="btn btn-success"
            onClick={handleSendSms}
            disabled={isSubmitting || isSendingSms}
            style={{ flex: 1 }}
          >
            {isSendingSms ? 'Sending SMS...' : 'Send SMS'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default MessageComposer;
