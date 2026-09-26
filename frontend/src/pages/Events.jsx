import EventCard from '../components/EventCard'
import { events } from '../data/events'
import './Events.css'

function Events() {
  return (
    <div className="events-page">
      <div className="events-header">
        <h1>Discover events</h1>
        <p>Handpicked conferences, meetups, workshops and festivals happening around you.</p>
      </div>
      <div className="events-grid">
        {events.map((event) => (
          <EventCard key={event.id} event={event} />
        ))}
      </div>
    </div>
  )
}

export default Events
