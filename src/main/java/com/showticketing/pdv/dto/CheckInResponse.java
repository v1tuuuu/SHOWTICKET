package com.showticketing.pdv.dto;

import com.showticketing.pdv.enums.TicketStatus;
import com.showticketing.pdv.enums.TicketType;
import java.time.LocalDateTime;

public class CheckInResponse {

    private boolean success;
    private String message;
    private String ticketCode;
    private String eventName;
    private String sectorName;
    private TicketType ticketType;
    private String holderName;
    private TicketStatus status;
    private LocalDateTime checkedInAt;

    public CheckInResponse() {}

    public static CheckInResponse ok(String message, String ticketCode, String eventName, String sectorName, TicketType ticketType, String holderName, LocalDateTime checkedInAt) {
        CheckInResponse r = new CheckInResponse();
        r.success = true;
        r.message = message;
        r.ticketCode = ticketCode;
        r.eventName = eventName;
        r.sectorName = sectorName;
        r.ticketType = ticketType;
        r.holderName = holderName;
        r.status = TicketStatus.CHECKED_IN;
        r.checkedInAt = checkedInAt;
        return r;
    }

    public static CheckInResponse fail(String message, String ticketCode, TicketStatus status) {
        CheckInResponse r = new CheckInResponse();
        r.success = false;
        r.message = message;
        r.ticketCode = ticketCode;
        r.status = status;
        return r;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getSectorName() { return sectorName; }
    public void setSectorName(String sectorName) { this.sectorName = sectorName; }

    public TicketType getTicketType() { return ticketType; }
    public void setTicketType(TicketType ticketType) { this.ticketType = ticketType; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }
}
