package br.com.ifmg.event_manager.dtos;

import java.time.LocalDate;

public class TicketDTO {
    private EventDTO event;
    private AddressDTO address;
    private TableEntityDTO table;
    private String guestName;
    private String urlPhoto;
    private Boolean confirmed;

    public TicketDTO() {
    }

    public TicketDTO(EventDTO event, AddressDTO address, TableEntityDTO table, String guestName, String urlPhoto, Boolean confirmed) {
        this.event = event;
        this.address = address;
        this.table = table;
        this.guestName = guestName;
        this.urlPhoto = urlPhoto;
        this.confirmed = confirmed;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public TableEntityDTO getTable() {
        return table;
    }

    public void setTable(TableEntityDTO table) {
        this.table = table;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }
}