import { Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '../../stores/authStore';
import { useAppStore } from '../../stores/appStore';
import { Home, Search, Heart, MessageCircle, User } from 'lucide-react';
import './BottomNav.css';

export default function BottomNav() {
  const location = useLocation();
  const { user } = useAuthStore();
  const { unreadCount } = useAppStore();

  const isActive = (path: string) => {
    if (path === '/' && location.pathname !== '/') return false;
    return location.pathname.startsWith(path);
  };

  const getProfileLink = () => {
    if (!user) return '/login';
    switch (user.role) {
      case 'admin': return '/admin';
      case 'landlord': return '/landlord';
      case 'tenant': return '/tenant';
      default: return '/profile';
    }
  };

  return (
    <nav className="bottom-nav">
      <Link to="/" className={`bottom-nav-item ${isActive('/') ? 'active' : ''}`}>
        <Home size={24} />
        <span>Trang chủ</span>
      </Link>
      <Link to="/rooms" className={`bottom-nav-item ${isActive('/rooms') ? 'active' : ''}`}>
        <Search size={24} />
        <span>Tìm phòng</span>
      </Link>
      {user && (
        <Link to="/favorites" className={`bottom-nav-item ${isActive('/favorites') ? 'active' : ''}`}>
          <Heart size={24} />
          <span>Yêu thích</span>
        </Link>
      )}
      {user && (
        <Link to="/chat" className={`bottom-nav-item ${isActive('/chat') ? 'active' : ''}`}>
          <div className="bottom-nav-icon-wrapper">
            <MessageCircle size={24} />
            {unreadCount > 0 && (
              <span className="bottom-nav-badge">{unreadCount > 9 ? '9+' : unreadCount}</span>
            )}
          </div>
          <span>Tin nhắn</span>
        </Link>
      )}
      <Link to={getProfileLink()} className={`bottom-nav-item ${['/login', '/admin', '/landlord', '/tenant', '/profile'].some(p => location.pathname.startsWith(p)) ? 'active' : ''}`}>
        <User size={24} />
        <span>Tài khoản</span>
      </Link>
    </nav>
  );
}
