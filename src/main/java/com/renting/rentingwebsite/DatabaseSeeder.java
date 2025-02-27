package com.renting.rentingwebsite;

import com.renting.rentingwebsite.entities.Client;
import com.renting.rentingwebsite.entities.Rentable;
import com.renting.rentingwebsite.entities.Rentals;
import com.renting.rentingwebsite.repository.ClientRepository;
import com.renting.rentingwebsite.repository.RentableRepository;
import com.renting.rentingwebsite.repository.RentalsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DatabaseSeeder {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RentableRepository rentableRepository;

    @Autowired
    private RentalsRepository rentalsRepository;


    @PostConstruct
    @Transactional
    public void seedDatabase() {
        try {
            Client client1 = null;
            Client client2 = null;
            Rentable rentable1 = null;
            Rentable rentable2 = null;

            if (clientRepository.count() == 0) {
                client1 = clientRepository.save(new Client("John Doe", "john@example.com"));
                client2 = clientRepository.save(new Client("Jane Doe", "jane@example.com"));
            } else {
                client1 = clientRepository.findById(1L).orElseThrow(RuntimeException::new);
                client2 = clientRepository.findById(2L).orElseThrow(RuntimeException::new);
            }

            if (rentableRepository.count() == 0) {
                rentable1 = rentableRepository.save(new Rentable(
                        "Slushice Maskine, 3 kammers, 15 liters, mærke Vevo",
                        "Slushice",
                        "Slushice Maskine der indeholder 3 kammers til 3 smage, i alt 15 liter fra Vevo",
                        "",
                        150
                ));
                rentable2 = rentableRepository.save(new Rentable(
                        "Royal Catering popcorn maskine i rød",
                        "Popcorn",
                        "En fantastisk popcorn maskine i rød.",
                        "",
                        100
                ));

            }else {
                rentable1 = rentableRepository.findById(1L).orElseThrow(RuntimeException::new);
                rentable2 = rentableRepository.findById(2L).orElseThrow(RuntimeException::new);
            }

            if (rentalsRepository.count() == 0) {
                LocalDate start_date = LocalDate.now().plusDays(5);
                LocalDate end_date = LocalDate.now().plusDays(10);
                rentalsRepository.save(
                        new Rentals(
                                start_date,
                                end_date,
                                true,
                                client1,
                                rentable1
                        )
                );
            }
        } catch (Exception e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
