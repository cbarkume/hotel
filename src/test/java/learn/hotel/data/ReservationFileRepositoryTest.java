package learn.hotel.data;

import learn.hotel.models.Guest;
import learn.hotel.models.Host;
import learn.hotel.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationFileRepositoryTest {

    static final String SEED_PATH = "./data/reservation_test/reservation-seed.csv";
    static final String TEST_PATH = "./data/reservation_test/reservation-test.csv";
    static final String TEST_DIR_PATH = "./data/reservation_test";
    static final int RESERVATION_COUNT = 16;

    GuestFileRepository guestRepo = new GuestFileRepository("./data/guests.csv");
    HostFileRepository hostRepo = new HostFileRepository("./data/hosts.csv");
    ReservationFileRepository reservationRepo = new ReservationFileRepository(TEST_DIR_PATH, guestRepo);

    Guest testGuest = guestRepo.findById(133);
    Host testHost = hostRepo.findById("ffe5c6f3-4321-435c-93ab-35ae53f9fed8");
    Reservation reservation = new Reservation(RESERVATION_COUNT + 1, LocalDate.of(2020,10,6), LocalDate.of(2020,10,14), testHost, testGuest);

    @BeforeEach
    void setup() throws IOException {
        reservation.setTotal(reservation.calculateTotal());
        testGuest.setGuestId(133);
        testHost.setId("reservation-test");
        Path seedPath = Paths.get(SEED_PATH);
        Path testPath = Paths.get(TEST_PATH);
        Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void shouldFindByHostGuest() {
        Reservation reservation = reservationRepo.findByHostGuest(testHost,testGuest);
        assertTrue(reservation.getId() == 1);
    }

    @Test
    void shouldNotFindWhenInvalidHost() {
        testHost.setId("bsuhfou");
        Reservation reservation = reservationRepo.findByHostGuest(testHost,testGuest);
        assertNull(reservation);
    }

    @Test
    void shouldNotFindWhenInvalidGuest() {
        testGuest.setGuestId(23904);
        Reservation reservation = reservationRepo.findByHostGuest(testHost,testGuest);
        assertNull(reservation);
    }

    @Test
    void shouldNotFindWhenBothInvalid() {
        testHost.setId("bsuhfou");
        testGuest.setGuestId(23904);
        Reservation reservation = reservationRepo.findByHostGuest(testHost,testGuest);
        assertNull(reservation);
    }

    @Test
    void shouldNotFindWhenHostNull() {
        Reservation reservation = reservationRepo.findByHostGuest(null,testGuest);
        assertNull(reservation);
    }

    @Test
    void shouldNotFindWhenGuestNull() {
        Reservation reservation = reservationRepo.findByHostGuest(testHost,null);
        assertNull(reservation);
    }

    @Test
    void shouldNotFindWhenBothNull() {
        Reservation reservation = reservationRepo.findByHostGuest(null,null);
        assertNull(reservation);
    }

    @Test
    void shouldFindByHost() {
        testHost.setId("reservation-test");
        List<Reservation> reservations = reservationRepo.findByHost(testHost);
        assertEquals(16, reservations.size());
    }

    @Test
    void shouldNotFindForInvalidHost() {
        testHost.setId("bsuhfou");
        List<Reservation> reservations = reservationRepo.findByHost(testHost);
        assertEquals(0, reservations.size());
    }

    @Test
    void shouldNotFindForNullHost() {
        List<Reservation> reservations = reservationRepo.findByHost(null);
        assertEquals(0,reservations.size());
    }

    @Test
    void shouldAdd() throws DataException {
        Reservation reservationResult = reservationRepo.add(reservation);
        assertEquals(reservationResult,reservation);
        assertEquals(17, reservationRepo.findByHost(testHost).size());
    }

    @Test
    void shouldNotAddNullReservation() throws DataException {
        Reservation reservationResult = reservationRepo.add(null);
        assertNull(reservationResult);
        assertEquals(16, reservationRepo.findByHost(testHost).size());
    }

    @Test
    void shouldEdit() throws DataException {
        reservationRepo.add(reservation);
        reservation.setEndDate(LocalDate.of(2020,11,1));
        boolean success = reservationRepo.edit(reservation);
        assertTrue(success);
        assertEquals(17, reservationRepo.findByHost(testHost).size());
    }

    @Test
    void shouldNotEditNullReservation() throws DataException {
        boolean success = reservationRepo.edit(null);
        assertFalse(success);
        assertEquals(16, reservationRepo.findByHost(testHost).size());
    }

    @Test
    void shouldCancel() throws DataException {
        reservationRepo.add(reservation);

        Reservation reservationResult = reservationRepo.cancel(reservation);
        assertEquals(reservationResult,reservation);
        assertEquals(16, reservationRepo.findByHost(testHost).size());
    }

    @Test
    void shouldNotCancelNullReservation() throws DataException {
        Reservation reservationResult = reservationRepo.cancel(null);
        assertNull(reservationResult);
        assertEquals(16, reservationRepo.findByHost(testHost).size());
    }
}