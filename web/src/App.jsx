import { useState } from "react";
import { ArrowRight, BusFront, CalendarDays, Check, ChevronRight, Clock3, MapPin, Search, ShieldCheck, TicketCheck } from "lucide-react";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";
const tomorrow = new Date();
tomorrow.setDate(tomorrow.getDate() + 1);
const defaultDate = `${tomorrow.getFullYear()}-${String(tomorrow.getMonth() + 1).padStart(2, "0")}-${String(tomorrow.getDate()).padStart(2, "0")}`;

async function api(path, options) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  const payload = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(payload.message || "Something went wrong. Please try again.");
  }
  return payload;
}

function money(amount, currency = "KES") {
  return new Intl.NumberFormat("en-KE", { style: "currency", currency, maximumFractionDigits: 0 }).format(amount);
}

function formatTime(value) {
  return new Intl.DateTimeFormat("en-KE", { hour: "numeric", minute: "2-digit" }).format(new Date(value));
}

function App() {
  const [from, setFrom] = useState("Nairobi");
  const [to, setTo] = useState("Kisumu");
  const [date, setDate] = useState(defaultDate);
  const [trips, setTrips] = useState([]);
  const [seats, setSeats] = useState([]);
  const [selectedTrip, setSelectedTrip] = useState(null);
  const [selectedSeat, setSelectedSeat] = useState(null);
  const [passengerName, setPassengerName] = useState("");
  const [passengerPhone, setPassengerPhone] = useState("");
  const [booking, setBooking] = useState(null);
  const [lookupReference, setLookupReference] = useState("");
  const [notice, setNotice] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function searchTrips(event) {
    event.preventDefault();
    setError("");
    setNotice("");
    setBooking(null);
    setSelectedTrip(null);
    setSelectedSeat(null);
    setSeats([]);
    setBusy(true);
    try {
      const query = new URLSearchParams({ from, to, date });
      setTrips(await api(`/trips?${query}`));
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  async function chooseTrip(trip) {
    setError("");
    setNotice("");
    setBooking(null);
    setSelectedSeat(null);
    setSelectedTrip(trip);
    setBusy(true);
    try {
      setSeats(await api(`/trips/${trip.id}/seats`));
    } catch (requestError) {
      setError(requestError.message);
      setSelectedTrip(null);
    } finally {
      setBusy(false);
    }
  }

  async function reserveSeat(event) {
    event.preventDefault();
    if (!selectedTrip || !selectedSeat) return;
    setError("");
    setBusy(true);
    try {
      const confirmed = await api("/bookings", {
        method: "POST",
        body: JSON.stringify({
          tripId: selectedTrip.id,
          seatId: selectedSeat.id,
          passengerName,
          passengerPhone,
        }),
      });
      setBooking(confirmed);
      setSelectedSeat(null);
      const query = new URLSearchParams({ from, to, date });
      const [updatedTrips, updatedSeats] = await Promise.all([
        api(`/trips?${query}`),
        api(`/trips/${selectedTrip.id}/seats`),
      ]);
      setTrips(updatedTrips);
      setSeats(updatedSeats);
    } catch (requestError) {
      setError(requestError.message);
      if (selectedTrip) setSeats(await api(`/trips/${selectedTrip.id}/seats`).catch(() => seats));
    } finally {
      setBusy(false);
    }
  }

  async function findBooking(event) {
    event.preventDefault();
    setError("");
    setNotice("");
    setBooking(null);
    setSelectedTrip(null);
    setSelectedSeat(null);
    setSeats([]);
    setBusy(true);
    try {
      setBooking(await api(`/bookings/${encodeURIComponent(lookupReference.trim())}`));
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <a className="brand" href="#" aria-label="Easy Coach Kenya home">
          <span className="brand-mark"><BusFront size={21} strokeWidth={2.2} /></span>
          <span className="brand-name">easy<span>coach</span><small>KENYA</small></span>
        </a>
        <nav className="top-nav" aria-label="Main navigation">
          <a href="#trips">Book a trip</a>
          <a href="#manage">Manage booking</a>
        </nav>
        <a className="help-link" href="#manage">Need help? <ChevronRight size={15} /></a>
      </header>

      <main>
        <section className="hero">
          <div className="hero-copy">
            <div className="eyebrow"><span className="eyebrow-line" /> YOUR JOURNEY, MADE EASY</div>
            <h1>Good journeys<br />start <em>right here.</em></h1>
            <p>Find your next trip, choose the seat you love, and get ready to go.</p>
            <div className="hero-perks">
              <span><Check size={15} /> Easy seat selection</span>
              <span><Check size={15} /> Instant confirmation</span>
            </div>
          </div>
          <div className="route-art" aria-hidden="true">
            <div className="sun" />
            <div className="hill hill-back" />
            <div className="hill hill-front" />
            <div className="road"><span /></div>
            <div className="art-bus"><BusFront size={80} strokeWidth={1.2} /></div>
            <span className="art-label">THE ROAD IS YOURS</span>
          </div>
          <div className="hero-bottom">
            <span><MapPin size={16} /> Connecting the places that matter</span>
            <span className="sample-note">DEMO BOOKING EXPERIENCE | SAMPLE ROUTES & FARES</span>
          </div>
        </section>

        <section className="search-wrap" id="trips">
          <form className="search-card" onSubmit={searchTrips}>
            <div className="search-heading">
              <div><span className="section-kicker">PLAN YOUR JOURNEY</span><h2>Where would you like to go?</h2></div>
              <span className="search-step"><span>01</span> SEARCH TRIPS</span>
            </div>
            <div className="search-fields">
              <label className="field">
                <span>FROM</span>
                <span className="input-icon"><MapPin size={17} />
                  <select value={from} onChange={(event) => setFrom(event.target.value)}>
                    <option>Nairobi</option><option>Kisumu</option><option>Mombasa</option>
                  </select>
                </span>
              </label>
              <button className="swap-button" type="button" aria-label="Swap departure and destination" onClick={() => { setFrom(to); setTo(from); }}>
                <ArrowRight size={17} />
              </button>
              <label className="field">
                <span>TO</span>
                <span className="input-icon"><MapPin size={17} />
                  <select value={to} onChange={(event) => setTo(event.target.value)}>
                    <option>Kisumu</option><option>Mombasa</option><option>Nairobi</option>
                  </select>
                </span>
              </label>
              <label className="field date-field">
                <span>TRAVEL DATE</span>
                <span className="input-icon"><CalendarDays size={17} />
                  <input type="date" min={defaultDate} value={date} onChange={(event) => setDate(event.target.value)} required />
                </span>
              </label>
              <button className="search-button" type="submit" disabled={busy}>
                <Search size={18} /> {busy ? "Please wait" : "Find a trip"}
              </button>
            </div>
          </form>
        </section>

        {(error || notice) && <div className={`feedback ${error ? "feedback-error" : ""}`} role="status">{error || notice}</div>}

        {trips.length > 0 && (
          <section className="results-section">
            <div className="results-title">
              <div><span className="section-kicker">AVAILABLE DEPARTURES</span><h2>{from} <ArrowRight size={21} /> {to}</h2></div>
              <span>{new Intl.DateTimeFormat("en-KE", { dateStyle: "full" }).format(new Date(`${date}T12:00:00`))}</span>
            </div>
            <div className="trip-list">
              {trips.map((trip) => (
                <article className={`trip-card ${selectedTrip?.id === trip.id ? "trip-card-active" : ""}`} key={trip.id}>
                  <div className="trip-time"><strong>{formatTime(trip.departureTime)}</strong><span>{trip.origin}</span></div>
                  <div className="trip-duration"><span>{trip.routeName}</span><div><i /><span className="duration-line" /><i /></div><small>{formatTime(trip.arrivalTime)}</small></div>
                  <div className="trip-time"><strong>{formatTime(trip.arrivalTime)}</strong><span>{trip.destination}</span></div>
                  <div className="trip-seat-count"><BusFront size={15} /> {trip.availableSeats} seats left</div>
                  <div className="trip-price"><strong>{money(trip.fare, trip.currency)}</strong><span>per passenger</span></div>
                  <button className="choose-button" onClick={() => chooseTrip(trip)} disabled={busy}>
                    {selectedTrip?.id === trip.id ? "Selected" : "Choose seat"} <ChevronRight size={16} />
                  </button>
                </article>
              ))}
            </div>
          </section>
        )}

        {trips.length === 0 && !error && !busy && (
          <section className="empty-state"><span className="empty-icon"><Search size={21} /></span><div><strong>Ready when you are</strong><p>Choose your route and travel date to see available sample trips.</p></div></section>
        )}

        {selectedTrip && (
          <section className="booking-section">
            <div className="section-heading">
              <div><span className="section-kicker">STEP 2 | SELECT A SEAT</span><h2>Pick your place on board</h2></div>
              <span className="selected-route">{selectedTrip.origin} <ArrowRight size={15} /> {selectedTrip.destination}</span>
            </div>
            <div className="seat-and-form">
              <div className="seat-panel">
                <div className="bus-front"><BusFront size={19} /><span>FRONT OF BUS</span></div>
                <div className="seat-grid">
                  {seats.map((seat) => (
                    <button className={`seat ${!seat.available ? "seat-taken" : ""} ${selectedSeat?.id === seat.id ? "seat-selected" : ""}`}
                      key={seat.id} disabled={!seat.available} onClick={() => setSelectedSeat(seat)}
                      aria-label={`Seat ${seat.id}${seat.available ? "" : ", booked"}`}>
                      {seat.id}
                    </button>
                  ))}
                </div>
                <div className="seat-legend"><span><i className="legend-available" /> Available</span><span><i className="legend-selected" /> Selected</span><span><i className="legend-taken" /> Booked</span></div>
              </div>
              {booking ? (
                <div className="passenger-form">
                  <span className="section-kicker">RESERVATION SAVED</span>
                  <h3>Seat {booking.seatId} is reserved.</h3>
                  <p className="reservation-reference">Keep this reference: <strong>{booking.bookingReference}</strong></p>
                  <small className="disclaimer"><ShieldCheck size={14} /> No online payment is taken in this demo.</small>
                </div>
              ) : (
                <form className="passenger-form" onSubmit={reserveSeat}>
                  <span className="section-kicker">PASSENGER DETAILS</span>
                  <h3>Who's travelling?</h3>
                  <label>Full name<input value={passengerName} onChange={(event) => setPassengerName(event.target.value)} autoComplete="name" required maxLength={120} placeholder="As shown on your ID" /></label>
                  <label>Mobile number<input value={passengerPhone} onChange={(event) => setPassengerPhone(event.target.value)} autoComplete="tel" required placeholder="0712345678 or +254712345678" /></label>
                  <div className="fare-summary"><span>Seat {selectedSeat?.id || "-"} - Pay at station</span><strong>{money(selectedTrip.fare, selectedTrip.currency)}</strong></div>
                  <button className="confirm-button" type="submit" disabled={!selectedSeat || busy}>{busy ? "Reserving..." : "Confirm reservation"} <ArrowRight size={17} /></button>
                  <small className="disclaimer"><ShieldCheck size={14} /> No online payment is taken in this demo.</small>
                </form>
              )}
            </div>
          </section>
        )}

        {booking && (
          <section className="confirmation" aria-live="polite">
            <span className="confirmation-icon"><TicketCheck size={25} /></span>
            <div className="confirmation-copy">
              <span className="section-kicker">RESERVATION CONFIRMED</span>
              <h2>You're on your way, {booking.passengerName.split(" ")[0]}.</h2>
              <p>{booking.origin} to {booking.destination} - Seat {booking.seatId} - {formatTime(booking.departureTime)}</p>
              <span className="reference">Booking reference <strong>{booking.bookingReference}</strong></span>
            </div>
            <button className="copy-button" onClick={() => navigator.clipboard?.writeText(booking.bookingReference)}>Copy reference</button>
          </section>
        )}

        <section className="manage-section" id="manage">
          <div><span className="section-kicker">ALREADY BOOKED?</span><h2>Your trip, at your fingertips.</h2><p>Enter your booking reference to view your reservation.</p></div>
          <form className="lookup-form" onSubmit={findBooking}>
            <label htmlFor="booking-ref">BOOKING REFERENCE</label>
            <div><input id="booking-ref" value={lookupReference} onChange={(event) => setLookupReference(event.target.value)} required placeholder="Paste your reference" /><button type="submit" disabled={busy}>Look up <ArrowRight size={16} /></button></div>
          </form>
        </section>

        {booking && (
          <section className="ticket-card">
            <div className="ticket-heading"><TicketCheck size={18} /><strong>Booking details</strong><span>{booking.status}</span></div>
            <div className="ticket-details">
              <div><span>PASSENGER</span><strong>{booking.passengerName}</strong></div>
              <div><span>JOURNEY</span><strong>{booking.origin} to {booking.destination}</strong></div>
              <div><span>DEPARTURE</span><strong>{new Date(booking.departureTime).toLocaleString("en-KE", { dateStyle: "medium", timeStyle: "short" })}</strong></div>
              <div><span>SEAT</span><strong>{booking.seatId}</strong></div>
              <div><span>FARE</span><strong>{money(booking.amount, booking.currency)}</strong></div>
              <div><span>PAYMENT</span><strong>{booking.paymentStatus === "PAY_AT_STATION" ? "Pay at station" : booking.paymentStatus}</strong></div>
            </div>
          </section>
        )}

        <section className="trust-row">
          <div><span className="trust-icon"><Clock3 size={17} /></span><span><strong>Plan with confidence</strong><small>See your departure details upfront</small></span></div>
          <div><span className="trust-icon"><TicketCheck size={17} /></span><span><strong>Your seat, reserved</strong><small>Instant booking reference</small></span></div>
          <div><span className="trust-icon"><ShieldCheck size={17} /></span><span><strong>Simple & straightforward</strong><small>Clear fares, no surprises</small></span></div>
        </section>
      </main>

      <footer><a className="brand footer-brand" href="#"><span className="brand-mark"><BusFront size={17} /></span><span className="brand-name">easy<span>coach</span><small>KENYA</small></span></a><span>Sample booking experience. Routes, fares and operator affiliation are not verified.</span><a href="#trips">Back to top</a></footer>
    </div>
  );
}

export default App;
