package com.renting.rentingwebsite;

import com.renting.rentingwebsite.entities.*;
import com.renting.rentingwebsite.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DatabaseSeeder {

    private final UserRepository userRepository;

    private final RentableItemRepository rentableItemRepository;

    private final ReservationRepository reservationRepository;

    private final RentableItemImageRepository rentableItemImageRepository;

    private final RentableItemSpecificationKeyRepository rentableItemSpecificationKeyRepository;

    private final RentableItemSpecificationRepository rentableItemSpecificationRepository;

    public DatabaseSeeder(
            UserRepository userRepository,
            RentableItemRepository rentableItemRepository,
            ReservationRepository reservationRepository,
            RentableItemImageRepository rentableItemImageRepository,
            RentableItemSpecificationKeyRepository rentableItemSpecificationKeyRepository,
            RentableItemSpecificationRepository rentableItemSpecificationRepository
    ) {
        this.userRepository = userRepository;
        this.rentableItemRepository = rentableItemRepository;
        this.reservationRepository = reservationRepository;
        this.rentableItemImageRepository = rentableItemImageRepository;
        this.rentableItemSpecificationKeyRepository = rentableItemSpecificationKeyRepository;
        this.rentableItemSpecificationRepository = rentableItemSpecificationRepository;
    }


    @PostConstruct
    @Transactional
    public void seedDatabase() {
        try {
            User user1 = null;
            User user2 = null;
            RentableItem rentableItem1 = null;
            RentableItem rentableItem2 = null;

            if (userRepository.count() == 0) {
                user1 = userRepository.save(new User("John Doe", "john@example.com", "password", "cus_RtEho0dcKZ0yMj"));
                user2 = userRepository.save(new User("Jane Doe", "jane@example.com", "password", "cus_RtEi7G8Hopajil"));
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

            if (rentableItemImageRepository.count() == 0) {
                rentableItemImageRepository.save(new RentableItemImage("image1.png", rentableItem1, 0));
                rentableItemImageRepository.save(new RentableItemImage("image2.png", rentableItem1, 1));
                rentableItemImageRepository.save(new RentableItemImage("image3.png", rentableItem2, 0));
            }

            var category1 = new RentableItemSpecificationKey("Chambers");
            var category2 = new RentableItemSpecificationKey("Volume");
            var category3 = new RentableItemSpecificationKey("Capacity");
            var category4 = new RentableItemSpecificationKey("Color");
            var category5 = new RentableItemSpecificationKey("Manufacturer");


            if (rentableItemSpecificationKeyRepository.count() == 0) {
                rentableItemSpecificationKeyRepository.save(category1);
                rentableItemSpecificationKeyRepository.save(category2);
                rentableItemSpecificationKeyRepository.save(category3);
                rentableItemSpecificationKeyRepository.save(category4);
                rentableItemSpecificationKeyRepository.save(category5);
            }

            if (rentableItemSpecificationRepository.count() == 0) {
                rentableItemSpecificationRepository.save(new RentableItemSpecification(
                        rentableItem1, category1, "3 Kammers"
                ));
                rentableItemSpecificationRepository.save(new RentableItemSpecification(
                        rentableItem1, category2, "15 Liter"
                ));
                rentableItemSpecificationRepository.save(new RentableItemSpecification(
                        rentableItem1, category4, "Hvid"
                ));
                rentableItemSpecificationRepository.save(new RentableItemSpecification(
                        rentableItem1, category5, "Vevo"
                ));

                rentableItemSpecificationRepository.save(new RentableItemSpecification(
                        rentableItem2, category4, "Rød"
                ));

                rentableItemSpecificationRepository.save(new RentableItemSpecification(
                        rentableItem2, category5, "Royal Catering"
                ));
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
