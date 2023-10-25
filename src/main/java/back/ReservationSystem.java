package back;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The `ReservationSystem` class manages reservations and table availability at a restaurant.
 * It provides methods for adding, removing, and updating reservations, as well as checking
 * the availability of tables and managing reservation data from JSON files.
 */

public class ReservationSystem {
    private final List<Reservation> reservations;
    private final List<Reservation> tables;

    /**
     * Creates a new instance of the `ReservationSystem` class.
     * Initializes the list of reservations and reads table data from a JSON file.
     */
    public ReservationSystem() {
        reservations = new ArrayList<>();
        tables = readReservationDataFromJson();
    }


    private void removeReservation(Reservation reservation) {
        if (reservations.remove(reservation)) {
            updateTableAvailability(reservation.getTableNumber(), true);
            System.out.println("Reservation removed: " + reservation);
        } else {
            System.out.println("Reservation not found: " + reservation);
        }
    }

    public boolean isTableAvailable(int tableNumber) {
        if (tables != null) {
            for (Reservation table : tables) {
                if (table.getTableNumber() == tableNumber) {
                    return table.isAvailable();
                }
            }
        }
        return false; // Table not found, consider as unavailable
    }


    private void updateTableAvailability(int tableNumber, boolean isAvailable) {
        for (Reservation table : tables) {
            if (table.getTableNumber() == tableNumber) {
                table.setAvailable(isAvailable);
                break;
            }
        }
    }

    /**
     * Reads table data from a JSON file and initializes table availability.
     *
     * @return A list of table reservations.
     */

    private List<Reservation> readReservationDataFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Reservation> reservations = objectMapper.readValue(new File("src/main/resources/tables.json"), new TypeReference<>() {
            });

            // Set the availability property of each table for each reservation
            for (Reservation reservation : reservations) {
                int tableNumber = reservation.getTableNumber();
                boolean isAvailable = isTableAvailable(tableNumber);
                reservation.setAvailable(isAvailable);

                // Assign category based on table number
                if (tableNumber >= 1 && tableNumber <= 5) {
                    reservation.setCategory("Normal");
                } else if (tableNumber >= 6 && tableNumber <= 10) {
                    reservation.setCategory("Special Needs");
                }
            }

            return reservations;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(); // Return an empty list if JSON reading fails
    }

    public static void validateTableBookingTime(String time) {
        if (!time.matches("^([01]?[0-9]|2[0-4]):[0-5][0-9]$")) {
            throw new IllegalArgumentException("Invalid table booking time: " + time);
        }
    }

    public String calculateCategory(int normalPeople, int disabilitiesPeople) {
        if (disabilitiesPeople >= normalPeople) {
            return "Special Needs";
        } else {
            return "Normal";
        }
    }

}
