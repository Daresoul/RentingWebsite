package com.renting.rentingwebsite;

import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.Reservation;
import com.renting.rentingwebsite.repository.UserRepository;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import com.renting.rentingwebsite.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DatabaseSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentableItemRepository rentableItemRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    @PostConstruct
    @Transactional
    public void seedDatabase() {
        try {
            User user1 = null;
            User user2 = null;
            RentableItem rentableItem1 = null;
            RentableItem rentableItem2 = null;

            if (userRepository.count() == 0) {
                user1 = userRepository.save(new User("John Doe", "john@example.com"));
                user2 = userRepository.save(new User("Jane Doe", "jane@example.com"));
            } else {
                user1 = userRepository.findById(1L).orElseThrow(RuntimeException::new);
                user2 = userRepository.findById(2L).orElseThrow(RuntimeException::new);
            }

            if (rentableItemRepository.count() == 0) {
                rentableItem1 = rentableItemRepository.save(new RentableItem(
                        "Slushice Maskine, 3 kammers, 15 liters, mærke Vevo",
                        "Slushice",
                        "Slushice Maskine der indeholder 3 kammers til 3 smage, i alt 15 liter fra Vevo",
                        "",
                        1500
                ));
                rentableItem2 = rentableItemRepository.save(new RentableItem(
                        "Royal Catering popcorn maskine i rød",
                        "Popcorn",
                        "En fantastisk popcorn maskine i rød.",
                        "",
                        2000
                ));

            }else {
                rentableItem1 = rentableItemRepository.findById(1L).orElseThrow(RuntimeException::new);
                rentableItem2 = rentableItemRepository.findById(2L).orElseThrow(RuntimeException::new);
            }

            if (reservationRepository.count() == 0) {
                LocalDate start_date = LocalDate.now().plusDays(5);
                LocalDate end_date = LocalDate.now().plusDays(10);
                reservationRepository.save(
                        new Reservation(
                                start_date,
                                end_date,
                                true,
                                true,
                                user1,
                                rentableItem1,
                                ""
                        )
                );
            }
        } catch (Exception e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
