package com.campussphere.repository;

import com.campussphere.entity.Booking;
import com.campussphere.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    java.util.Optional<Booking> findByIdForUpdate(@Param("id") Long id);

    List<Booking> findByUserId(Long userId);
    List<Booking> findByVenueId(Long venueId);
    boolean existsByVenueId(Long venueId);
    boolean existsByVenueIdAndStatusInAndStartTimeAfter(Long venueId, Collection<BookingStatus> statuses, LocalDateTime startTime);
    boolean existsByVenueIdAndStatusInAndStartTimeAfterAndAttendeesCountGreaterThan(Long venueId, Collection<BookingStatus> statuses, LocalDateTime startTime, Integer attendeesCount);
    List<Booking> findByVenueIdAndStartTimeLessThanAndEndTimeGreaterThan(Long venueId, LocalDateTime endTime, LocalDateTime startTime);
    List<Booking> findByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.venue.id = :venueId AND b.status IN (com.campussphere.entity.enums.BookingStatus.APPROVED, com.campussphere.entity.enums.BookingStatus.PENDING) AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findConflictingBookings(@Param("venueId") Long venueId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.venue.id = :venueId AND b.id <> :excludeId AND b.status IN (com.campussphere.entity.enums.BookingStatus.APPROVED, com.campussphere.entity.enums.BookingStatus.PENDING) AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findConflictingBookingsExcluding(@Param("venueId") Long venueId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("excludeId") Long excludeId);
}
