import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

class NotificationService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
  }

  connect(callback) {
    const socketFactory = () => new SockJS('http://localhost:8080/ws');
    
    this.stompClient = new Client({
      webSocketFactory: socketFactory,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        this.connected = true;
        console.log('Connected to WebSocket');
        
        this.stompClient.subscribe('/topic/notifications', (message) => {
          const notification = JSON.parse(message.body);
          callback({
            type: 'new_request',
            data: notification
          });
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers['message'], frame.body);
        this.connected = false;
      },
      onWebSocketClose: () => {
        this.connected = false;
        console.warn('WebSocket connection closed');
      },
      debug: (str) => {
        console.log('STOMP Debug:', str);
      }
    });
    
    this.stompClient.activate();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.connected = false;
      console.log('Disconnected from WebSocket');
    }
  }

  isConnected() {
    return this.connected;
  }
}

const notificationService = new NotificationService();
export default notificationService;
