package com.ashlesh.mycoolproject.repository;



import com.ashlesh.mycoolproject.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // Duplicate checks (same name + same address, ignoring upper/lower case)
    boolean existsByNameIgnoreCaseAndAddressIgnoreCase(String name, String address);

    boolean existsByNameIgnoreCaseAndAddressIgnoreCaseAndIdNot(String name, String address, Long id);

    // Filtering with pagination (Spring Data builds the query from the method name)
    Page<Restaurant> findByActive(boolean active, Pageable pageable);

    Page<Restaurant> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Restaurant> findByNameContainingIgnoreCaseAndActive(String name, boolean active, Pageable pageable);
}