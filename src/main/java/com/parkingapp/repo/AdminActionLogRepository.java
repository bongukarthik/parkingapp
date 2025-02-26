package com.parkingapp.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.parkingapp.model.AdminActionLog;

@Repository
public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {
    
    @Query("SELECT log FROM AdminActionLog log WHERE " +
            "(:actionType IS NULL OR log.actionType = :actionType) AND " +
            "(:performedBy IS NULL OR log.performedBy = :performedBy) AND " +
            "(:targetAdmin IS NULL OR log.targetAdmin = :targetAdmin) AND " +
            "(:startDate IS NULL OR log.timestamp >= :startDate) AND " +
            "(:endDate IS NULL OR log.timestamp <= :endDate)")
    List<AdminActionLog> findLogs(@Param("actionType") String actionType, 
                                  @Param("performedBy") String performedBy, 
                                  @Param("targetAdmin") String targetAdmin, 
                                  @Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
}
