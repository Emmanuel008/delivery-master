import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Navbar from './components/Navbar';
import UserForm from './components/UserForm';
import AdminDashboard from './components/AdminDashboard';
import Login from './components/Login';

// Component to conditionally show navbar
const AppContent = ({ children, showNavbar, navbarProps }) => {
  return (
    <>
      {showNavbar && <Navbar {...navbarProps} />}
      {children}
    </>
  );
};

// Protected Route Component
const ProtectedRoute = ({ children, isAuthenticated }) => {
  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [navbarState, setNavbarState] = useState({
    activeTab: 'dashboard',
    messagesCount: 0,
    unreadCount: 0
  });

  // Check authentication on mount
  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      setIsAuthenticated(true);
    }
  }, []);

  const handleLogin = (status) => {
    setIsAuthenticated(status);
  };

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    setIsAuthenticated(false);
  };

  const handleNavbarUpdate = (state) => {
    setNavbarState(state);
  };

  const handleTabChange = (tab) => {
    setNavbarState(prev => ({ ...prev, activeTab: tab }));
  };

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route 
            path="/" 
            element={
              <AppContent 
                showNavbar={true}
                navbarProps={{
                  activeTab: navbarState.activeTab,
                  onTabChange: handleTabChange,
                  messagesCount: navbarState.messagesCount,
                  unreadCount: navbarState.unreadCount,
                  onLogout: handleLogout
                }}
              >
                <UserForm />
              </AppContent>
            } 
          />
          <Route 
            path="/login" 
            element={
              isAuthenticated ? (
                <Navigate to="/admin" replace />
              ) : (
                <AppContent 
                  showNavbar={true}
                  navbarProps={{
                    activeTab: navbarState.activeTab,
                    onTabChange: handleTabChange,
                    messagesCount: navbarState.messagesCount,
                    unreadCount: navbarState.unreadCount,
                    onLogout: handleLogout
                  }}
                >
                  <Login onLogin={handleLogin} />
                </AppContent>
              )
            } 
          />
          <Route 
            path="/admin" 
            element={
              <ProtectedRoute isAuthenticated={isAuthenticated}>
                <AdminDashboard 
                  onNavbarUpdate={handleNavbarUpdate} 
                  activeTab={navbarState.activeTab} 
                  onTabChange={handleTabChange}
                  onLogout={handleLogout}
                />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
