package learn.hotel.data;

import learn.hotel.models.Guest;
import learn.hotel.models.State;

import java.util.ArrayList;
import java.util.List;

public class GuestRepositoryDouble implements GuestRepository {

    private final List<Guest> guests = new ArrayList<>();
    Guest GUEST = new Guest(1, "Cecilia", "Barkume", "ceciliabarkume@gmail.com", "(734) 5079310", State.MI);

    public GuestRepositoryDouble() {
        guests.add(GUEST);
    }

    @Override
    public List<Guest> findAll() {
        return guests;
    }

    @Override
    public Guest findById(int id) {
        return findAll().stream()
                .filter(i -> i.getGuestId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Guest add(Guest guest) throws DataException {
        return null;
    }

    @Override
    public boolean edit(Guest guest) throws DataException {
        return false;
    }

    @Override
    public Guest delete(Guest guest) throws DataException {
        return null;
    }
}
