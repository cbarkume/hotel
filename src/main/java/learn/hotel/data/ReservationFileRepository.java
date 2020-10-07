package learn.hotel.data;

import learn.hotel.models.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationFileRepository implements ReservationRepository {

    private static final String HEADER = "id,startDate,endDate,guestId,total";
    public String directory;
    GuestFileRepository guestRepo;

    public ReservationFileRepository(String directory, GuestFileRepository guestRepo) {
        this.directory = directory;
        this.guestRepo = guestRepo;
    }

    @Override
    public Reservation findByHostGuest(Host host, Guest guest) {
        List<Reservation> reservations = findByHost(host);

        if (reservations.size() == 0 || guest == null) {
            return null;
        }

        for (Reservation reservation : reservations) {
            if (guest.getGuestId() == reservation.getGuest().getGuestId()) {
                return reservation;
            }
        }

        return null;
    }

    @Override
    public List<Reservation> findByHost(Host host) {
        ArrayList<Reservation> result = new ArrayList<>();

        if (host == null) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath(host.getId())))) {

            reader.readLine(); // read header

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                String[] fields = line.split(",", -1);
                if (fields.length == 5) {
                    result.add(deserialize(fields, host));
                }
            }
        } catch (IOException ex) {
            // don't throw on read
        }
        return result;
    }

    @Override
    public Reservation add(Reservation reservation) throws DataException {
        if (reservation == null) {
            return null;
        }
        List<Reservation> all = findByHost(reservation.getHost());

        int nextId = all.stream()
                .mapToInt(Reservation::getId)
                .max()
                .orElse(0) + 1;

        reservation.setId(nextId);

        all.add(reservation);
        writeAll(all);
        return reservation;
    }

    @Override
    public boolean edit(Reservation reservation) throws DataException {
        if (reservation == null) {
            return false;
        }

        List<Reservation> all = findByHost(reservation.getHost());
        for (int i = 0; i < all.size(); i++) {
            if (reservation.getId() == all.get(i).getId()) {
                all.set(i, reservation);
                writeAll(all);
                return true;
            }
        }

        return false;
    }

    @Override
    public Reservation cancel(Reservation reservation) throws DataException {
        if (reservation ==  null) {
            return null;
        }

        List<Reservation> all = findByHost(reservation.getHost());
        for (int i = 0; i < all.size(); i++) {
            if (reservation.getId() == all.get(i).getId()) {
                all.remove(i);
                writeAll(all);
                return reservation;
            }
        }
        return null;
    }

    private void writeAll(List<Reservation> reservations) throws DataException {
        if (reservations.size() == 0) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(getFilePath(reservations.get(0).getHost().getId()))) {

            writer.println(HEADER);

            for (Reservation reservation : reservations) {
                writer.println(serialize(reservation));
            }
        } catch (FileNotFoundException ex) {
            throw new DataException(ex);
        }
    }

    private String getFilePath(String hostId) {
        return Paths.get(directory, hostId + ".csv").toString();
    }

    private String serialize(Reservation reservation) {
        return String.format("%s,%s,%s,%s,%s",
                reservation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getGuest().getGuestId(),
                reservation.getTotal());
    }

    private Reservation deserialize(String[] fields, Host host) {
        Reservation reservation = new Reservation();

        reservation.setId(Integer.parseInt(fields[0]));
        reservation.setStartDate(LocalDate.parse(fields[1]));
        reservation.setEndDate(LocalDate.parse(fields[2]));
        reservation.setHost(host);

        Guest guest = guestRepo.findById(Integer.parseInt(fields[3]));
        reservation.setGuest(guest);

        reservation.setTotal(new BigDecimal(fields[4]));

        return reservation;
    }

}
