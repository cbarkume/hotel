package learn.hotel.data;

import learn.hotel.models.*;

import java.util.List;

public interface ReservationRepository {
    Reservation findByHostGuest(Host host, Guest guest);

    List<Reservation> findByHost(Host host);

    Reservation add(Reservation reservation) throws DataException;

    boolean edit(Reservation reservation) throws DataException;

    Reservation cancel(Reservation reservation) throws DataException;
}
