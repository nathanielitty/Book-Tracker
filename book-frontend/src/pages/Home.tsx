import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Home: React.FC = () => {
  const { isAuthenticated } = useContext(AuthContext);

  if (isAuthenticated) {
    return (
      <div className="page">
        <div className="page-header">
          <h1 className="page-title">Welcome to BookTracker</h1>
          <p className="page-subtitle">
            Your personal library management system inspired by MyAnimeList
          </p>
          <div className="home-actions">
            <Link to="/dashboard" className="btn-primary">
              Go to Dashboard
            </Link>
            <Link to="/search" className="btn-secondary">
              Search Books
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="hero-section">
        <div className="hero-content">
          <h1 className="hero-title">BookTracker</h1>
          <p className="hero-subtitle">
            Track, rate, and discover your next favorite book
          </p>
          <p className="hero-description">
            A modern, MyAnimeList-inspired book tracking application that helps you 
            organize your reading life. Keep track of books you've read, want to read, 
            and are currently reading. Get personalized recommendations and discover 
            new books tailored to your taste.
          </p>
          <div className="hero-actions">
            <Link to="/register" className="btn-primary">
              Get Started
            </Link>
            <Link to="/login" className="btn-secondary">
              Login
            </Link>
          </div>
        </div>
        
        <div className="hero-image">
          <div className="book-stack">
            <div className="book book-1">📚</div>
            <div className="book book-2">📖</div>
            <div className="book book-3">📕</div>
            <div className="book book-4">📗</div>
            <div className="book book-5">📘</div>
          </div>
        </div>
      </div>

      <div className="features-section">
        <h2>Why BookTracker?</h2>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">📊</div>
            <h3>Track Your Progress</h3>
            <p>Keep detailed records of your reading journey with custom shelves and ratings</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🎯</div>
            <h3>Set Reading Goals</h3>
            <p>Challenge yourself with annual reading goals and track your progress</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🔍</div>
            <h3>Discover New Books</h3>
            <p>Get personalized recommendations based on your reading preferences</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📱</div>
            <h3>Modern Interface</h3>
            <p>Enjoy a beautiful, responsive design inspired by popular tracking platforms</p>
          </div>
        </div>
      </div>

      <div className="cta-section">
        <h2>Ready to start your reading journey?</h2>
        <p>Join thousands of readers who trust BookTracker to organize their library</p>
        <Link to="/register" className="btn-primary">
          Sign Up Now
        </Link>
      </div>
    </div>
  );
};

export default Home;
