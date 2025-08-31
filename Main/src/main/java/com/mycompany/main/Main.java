package com.mycompany.main;

import java.io.*;
import java.util.*;

class Room {
    int roomId;
    String category;
    boolean isBooked;

    public Room(int roomId, String category) {
        this.roomId = roomId;
        this.category = category;
        this.isBooked = false;
    }

    @Override
    public String toString() {
        return "Room ID: " + roomId + ", Category: " + category + ", Status: " + (isBooked ? "Booked" : "Available");
    }
}

class Reservation implements Serializable {
    int reservationId;
    String customerName;
    Room room;

    public Reservation(int reservationId, String customerName, Room room) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.room = room;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId + ", Customer: " + customerName + ", Room: [ID=" + room.roomId + ", Category=" + room.category + "]";
    }
}

class HotelSystem {
    private List<Room> rooms = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private int nextReservationId = 1;
    private static final String FILE_NAME = "reservations.dat";

    public HotelSystem() {
        rooms.add(new Room(101, "Standard"));
        rooms.add(new Room(102, "Deluxe"));
        rooms.add(new Room(201, "Suite"));
        rooms.add(new Room(202, "Standard"));
        rooms.add(new Room(301, "Deluxe"));

        loadReservations();
    }

    public void showAvailableRooms() {
        System.out.println("Available Rooms:");
        for (Room r : rooms) {
            if (!r.isBooked) System.out.println(r);
        }
    }

    public void makeReservation(String customer, String category) {
        for (Room r : rooms) {
            if (!r.isBooked && r.category.equalsIgnoreCase(category)) {
                r.isBooked = true;
                Reservation res = new Reservation(nextReservationId++, customer, r);
                reservations.add(res);
                saveReservations();
                System.out.println("Reservation successful! " + res);
                simulatePayment();
                return;
            }
        }
        System.out.println("No available rooms in category: " + category);
    }

    public void cancelReservation(int reservationId) {
        Iterator<Reservation> it = reservations.iterator();
        while (it.hasNext()) {
            Reservation res = it.next();
            if (res.reservationId == reservationId) {
                res.room.isBooked = false;
                it.remove();
                saveReservations();
                System.out.println("Reservation " + reservationId + " cancelled.");
                return;
            }
        }
        System.out.println("Reservation ID not found.");
    }

    public void viewReservations() {
        System.out.println("All Reservations:");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    private void simulatePayment() {
        System.out.println("Processing payment...");
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        System.out.println("Payment successful!\n");
    }

    private void saveReservations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(reservations);
            oos.writeInt(nextReservationId);
        } catch (IOException e) {
            System.out.println("Error saving reservations: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadReservations() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            reservations = (List<Reservation>) ois.readObject();
            nextReservationId = ois.readInt();
            for (Reservation res : reservations) {
                for (Room r : rooms) {
                    if (r.roomId == res.room.roomId) {
                        r.isBooked = true;
                        res.room = r;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading reservations: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        HotelSystem hs = new HotelSystem();
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Hotel Reservation System =====");
            System.out.println("1. Show Available Rooms");
            System.out.println("2. Make Reservation");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View Reservations");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt(); sc.nextLine();

            switch(choice) {
                case 1:
                    hs.showAvailableRooms();
                    break;
                case 2:
                    System.out.print("Enter customer name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter room category (Standard/Deluxe/Suite): ");
                    String cat = sc.nextLine();
                    hs.makeReservation(name, cat);
                    break;
                case 3:
                    System.out.print("Enter reservation ID to cancel: ");
                    int rid = sc.nextInt();
                    hs.cancelReservation(rid);
                    break;
                case 4:
                    hs.viewReservations();
                    break;
                case 5:
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while(choice != 5);
    }
}
