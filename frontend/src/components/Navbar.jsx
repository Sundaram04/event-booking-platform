import { Link } from 'react-router-dom'

function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-brand">Event Booking</div>
      <div className="navbar-links">
        <Link to="/">Home</Link>
        <Link to="/events">Events</Link>
        <Link to="/login">Login</Link>
        <Link to="/register">Register</Link>
      </div>
    </nav>
  )
}

export default Navbar
