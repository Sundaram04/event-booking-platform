import { Link } from 'react-router-dom'
import './Navbar.css'

function Navbar() {
  return (
    <header className="navbar">
      <Link to="/" className="navbar-brand">
        EVENT<span className="navbar-brand-accent">/</span>
      </Link>
      <nav className="navbar-links" aria-label="Main navigation">
        <Link to="/">Home</Link>
        <Link to="/events">Events</Link>
      </nav>
      <div className="navbar-actions">
        <Link to="/login" className="navbar-login">
          Log in
        </Link>
        <Link to="/register" className="btn btn-primary navbar-signup">
          Sign up
        </Link>
      </div>
    </header>
  )
}

export default Navbar
