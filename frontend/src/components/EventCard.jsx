import { Link } from 'react-router-dom'
import { formatEventDate } from '../utils/formatDate'
import './EventCard.css'

function EventCard({ event }) {
  const { id, title, category, description, date, time, location, city, price, availableSeats, capacity } = event

  const isSoldOut = availableSeats === 0
  const isAlmostFull = !isSoldOut && availableSeats <= capacity * 0.15

  let seatsClass = ''
  if (isSoldOut) seatsClass = 'is-sold-out'
  else if (isAlmostFull) seatsClass = 'is-almost-full'

  return (
    <Link to={`/events/${id}`} className="event-card" aria-label={`View details for ${title}`}>
      <div className={`event-card-visual event-card-visual--${category.toLowerCase()}`}>
        <span className="event-card-initial" aria-hidden="true">
          {title.charAt(0)}
        </span>
        <span className="event-card-date-badge">{formatEventDate(date)}</span>
      </div>
      <div className="event-card-body">
        <span className="event-card-category">{category}</span>
        <h3 className="event-card-title">{title}</h3>
        <p className="event-card-meta">
          {location}, {city} &middot; {time}
        </p>
        <p className="event-card-description">{description}</p>
        <div className="event-card-footer">
          <span className="event-card-price">{price === 0 ? 'Free' : `₹${price}`}</span>
          <span className={`event-card-seats ${seatsClass}`}>
            {isSoldOut ? 'Sold out' : `${availableSeats} seats left`}
          </span>
        </div>
        <span className="event-card-cta">
          View details <span aria-hidden="true">&rarr;</span>
        </span>
      </div>
    </Link>
  )
}

export default EventCard
