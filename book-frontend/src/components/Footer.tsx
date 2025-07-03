import React from 'react';
import { BookOpen, Github, Linkedin, Mail } from 'lucide-react';

const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-800 border-t border-gray-700 mt-auto">
      <div className="max-w-7xl mx-auto px-4 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Brand Section */}
          <div className="space-y-4">
            <div className="flex items-center space-x-2">
              <BookOpen className="text-blue-400" size={24} />
              <h4 className="text-xl font-bold text-white">BookTracker</h4>
            </div>
            <p className="text-gray-400">
              Your personal library management system for tracking reading progress and discovering new books.
            </p>
          </div>
          
          {/* Features */}
          <div className="space-y-4">
            <h5 className="text-lg font-semibold text-white">Features</h5>
            <ul className="space-y-2 text-gray-400">
              <li className="hover:text-white transition-colors cursor-pointer">📊 Track Reading Progress</li>
              <li className="hover:text-white transition-colors cursor-pointer">🔍 Discover New Books</li>
              <li className="hover:text-white transition-colors cursor-pointer">🎯 Set Reading Goals</li>
              <li className="hover:text-white transition-colors cursor-pointer">💡 Get Recommendations</li>
            </ul>
          </div>
          
          {/* Tech Stack */}
          <div className="space-y-4">
            <h5 className="text-lg font-semibold text-white">Built With</h5>
            <ul className="space-y-2 text-gray-400">
              <li className="hover:text-blue-400 transition-colors cursor-pointer">⚛️ React & TypeScript</li>
              <li className="hover:text-green-400 transition-colors cursor-pointer">🍃 Spring Boot</li>
              <li className="hover:text-blue-400 transition-colors cursor-pointer">🐘 PostgreSQL</li>
              <li className="hover:text-purple-400 transition-colors cursor-pointer">🎨 Tailwind CSS</li>
            </ul>
          </div>
          
          {/* Connect */}
          <div className="space-y-4">
            <h5 className="text-lg font-semibold text-white">Connect</h5>
            <div className="flex space-x-4">
              <a 
                href="https://github.com/nathaniel" 
                target="_blank" 
                rel="noopener noreferrer"
                className="text-gray-400 hover:text-white transition-colors"
              >
                <Github size={20} />
              </a>
              <a 
                href="https://linkedin.com/in/nathaniel" 
                target="_blank" 
                rel="noopener noreferrer"
                className="text-gray-400 hover:text-blue-400 transition-colors"
              >
                <Linkedin size={20} />
              </a>
              <a 
                href="mailto:nathaniel@example.com"
                className="text-gray-400 hover:text-green-400 transition-colors"
              >
                <Mail size={20} />
              </a>
            </div>
          </div>
        </div>
        
        {/* Bottom Section */}
        <div className="border-t border-gray-700 mt-8 pt-8 text-center space-y-2">
          <p className="text-gray-400">
            © 2025 BookTracker. Built with ❤️ by Nathaniel
          </p>
          <p className="text-sm text-gray-500">
            A full-stack portfolio project showcasing modern web development
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
