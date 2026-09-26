import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { events } from '../data/events'
import { formatEventDate } from '../utils/formatDate'
import './EventDetails.css'

function EventDetails() {
  const { id } = useParams()
  const [showBookingNotice, setShowBookingNotice] = useState(false)
  const event = events.find((item) => String(item.id) === id)

  if (!event) {
    return (
      <div className="page event-not-found">
        <h1>Event not found</h1>
        <p>We couldn&apos;t find the event you&apos;re looking for. It may have been removed or the link is incorrect.</p>
        <Link to="/events" className="btn btn-primary">
          Back to events
        </Link>
      </div>
    )
  }

  const { title, category, description, date, time, location, city, price, availableSeats, capacity } = event
  const isSoldOut = availableSeats === 0

  return (
    <div className="event-details-page">
      <Link to="/events" className="event-details-back">
        &larr; Back to events
      </Link>

      <div className={`event-details-visual event-details-visual--${category.toLowerCase()}`}>
        <span className="event-details-initial" aria-hidden="true">
          {title.charAt(0)}
        </span>
      </div>

      <div className="event-details-layout">
        <div className="event-details-main">
          <span className="event-details-category">{category}</span>
          <h1 className="event-details-title">{title}</h1>
          <p className="event-details-meta">
            {location}, {city} &middot; {formatEventDate(date)} &middot; {time}
          </p>
          <p className="event-details-description">{description}</p>
        </div>

        <aside className="event-details-panel">
          <div className="event-details-price">{price === 0 ? 'Free' : `₹${price}`}</div>

          <dl className="event-details-facts">
            <div>
              <dt>Date</dt>
              <dd>{formatEventDate(date)}</dd>
            </div>
            <div>
              <dt>Time</dt>
              <dd>{time}</dd>
            </div>
            <div>
              <dt>Location</dt>
              <dd>
                {location}, {city}
              </dd>
            </div>
            <div>
              <dt>Capacity</dt>
              <dd>{capacity} attendees</dd>
            </div>
            <div>
              <dt>Availability</dt>
              <dd className={isSoldOut ? 'is-sold-out' : ''}>{isSoldOut ? 'Sold out' : `${availableSeats} seats left`}</dd>
            </div>
          </dl>

          <button
            type="button"
            className="btn btn-primary event-details-cta"
            disabled={isSoldOut}
            onClick={() => setShowBookingNotice(true)}
          >
            {isSoldOut ? 'Sold out' : 'Book now'}
          </button>

          {showBookingNotice && !isSoldOut && (
            <p className="event-details-note" role="status">
              Booking will be available soon.
            </p>
          )}
        </aside>
      </div>
    </div>
  )
}

export default EventDetails
