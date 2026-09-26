import { Link } from 'react-router-dom'
import EventCard from '../components/EventCard'
import { events } from '../data/events'
import './Home.css'

function Home() {
  const featuredEvents = events.slice(0, 3)

  return (
    <div className="home-page">
      <section className="home-hero-section">
        <div className="home-hero-inner">
          <span className="home-hero-eyebrow">Live events</span>
          <h1 className="home-hero-title">Find something worth showing up for.</h1>
          <p className="home-hero-subtitle">
            Music, tech, art and community &mdash; curated events happening near you.
          </p>
          <Link to="/events" className="btn btn-primary">
            Browse events
          </Link>
        </div>
      </section>

      <section className="home-featured">
        <div className="home-featured-header">
          <h2>Featured events</h2>
          <Link to="/events" className="home-featured-link">
            View all events &rarr;
          </Link>
        </div>
        <div className="home-featured-grid">
          {featuredEvents.map((event) => (
            <EventCard key={event.id} event={event} />
          ))}
        </div>
      </section>
    </div>
  )
}

export default Home
