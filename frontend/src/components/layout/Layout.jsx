import { Outlet } from 'react-router-dom';
import Header from './Header';
import { useToast } from '../../contexts/ToastContext';

export default function Layout() {
  const { message, clearToast } = useToast();

  return (
    <div className="app">
      <Header />
      <main className="main">
        {message && (
          <div className={`global-message ${message.type === 'error' ? 'error-message' : 'success-message'}`}>
            <span>{message.text}</span>
            <button onClick={clearToast} className="message-close">&times;</button>
          </div>
        )}
        <Outlet />
      </main>
    </div>
  );
}
