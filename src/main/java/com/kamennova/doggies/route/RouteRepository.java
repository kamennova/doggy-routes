package com.kamennova.doggies.route;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByUserId(Long userId);

    @Query(value = "UPDATE routes SET is_active = false WHERE user_id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void hideRoutesByUserId(Long userId);

    @Query(value = "UPDATE routes SET is_active = true WHERE user_id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void showRoutesByUserId(Long userId);
}
