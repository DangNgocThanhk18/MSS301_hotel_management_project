package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Long customerId;
    private List<Long> roomIds;
    private Date expectedCheckInDate;
    private Date expectedCheckOutDate;
    private String note;


    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<Long> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(List<Long> roomIds) {
        this.roomIds = roomIds;
    }

    public Date getExpectedCheckInDate() {
        return expectedCheckInDate;
    }

    public void setExpectedCheckInDate(Date expectedCheckInDate) {
        this.expectedCheckInDate = expectedCheckInDate;
    }

    public Date getExpectedCheckOutDate() {
        return expectedCheckOutDate;
    }

    public void setExpectedCheckOutDate(Date expectedCheckOutDate) {
        this.expectedCheckOutDate = expectedCheckOutDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}