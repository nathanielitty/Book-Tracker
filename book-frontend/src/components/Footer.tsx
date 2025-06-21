import React from 'react';

const Footer: React.FC = () => {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-content">
          <div className="footer-section">
            <h4>BookTracker</h4>
            <p>Your personal library management system</p>
          </div>
          
          <div className="footer-section">
            <h5>Features</h5>
            <ul>
              <li>Track Reading Progress</li>
              <li>Discover New Books</li>
              <li>Set Reading Goals</li>
              <li>Get Recommendations</li>
            </ul>
          </div>
          
          <div className="footer-section">
            <h5>Built With</h5>
            <ul>
              <li>React & TypeScript</li>
              <li>Spring Boot</li>
              <li>PostgreSQL</li>
              <li>Modern CSS</li>
            </ul>
          </div>
          
          <div className="footer-section">
            <h5>Connect</h5>
            <ul>
              <li><a href="https://github.com/nathaniel" target="_blank" rel="noopener noreferrer">GitHub</a></li>
              <li><a href="https://linkedin.com/in/nathaniel" target="_blank" rel="noopener noreferrer">LinkedIn</a></li>
              <li><a href="mailto:nathaniel@example.com">Email</a></li>
            </ul>
          </div>
        </div>
        
        <div className="footer-bottom">
          <p>&copy; 2025 BookTracker. Built with ❤️ by Nathaniel</p>
          <p>A full-stack portfolio project showcasing modern web development</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
