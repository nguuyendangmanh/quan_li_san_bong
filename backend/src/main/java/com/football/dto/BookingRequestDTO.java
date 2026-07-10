package com.football.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingRequestDTO {
    private Integer fieldId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<BookingServiceItemDTO> services;

    public Integer getFieldId() { return fieldId; }
    public void setFieldId(Integer fieldId) { this.fieldId = fieldId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public List<BookingServiceItemDTO> getServices() { return services; }
    public void setServices(List<BookingServiceItemDTO> services) { this.services = services; }
}
