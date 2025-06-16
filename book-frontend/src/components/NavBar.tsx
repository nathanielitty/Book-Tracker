import React, { useContext } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const NavBar: React.FC = () => {
  const { isAuthenticated, logout } = useContext(AuthContext);
  const location = useLocation();

  const handleLogout = () => {
    logout();
  };

  // Check if a link is active
  const isActive = (path: string) => {
    return location.pathname === path ? 'active-link' : '';
  };

  const isShelfActive = () => {
    return location.pathname.startsWith('/shelf') ? 'active-link' : '';
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="nav-brand">
          📚 BookTracker
        </Link>

        {isAuthenticated && (
          <ul className="nav-links">
            <li>
              <Link to="/dashboard" className={isActive('/dashboard')}>
                Dashboard
              </Link>
            </li>
            <li>
              <Link to="/search" className={isActive('/search')}>
                Search
              </Link>
            </li>
            <li>
              <Link to="/shelf/reading" className={isShelfActive()}>
                My Library
              </Link>
            </li>
          </ul>
        )}
        
        <div className="nav-auth">
          {isAuthenticated ? (
            <>
              <span className="user-welcome">Welcome back!</span>
              <button onClick={handleLogout} className="logout-btn">
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className={isActive('/login')}>Login</Link>
              <Link to="/register" className={isActive('/register')}>Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default NavBar;