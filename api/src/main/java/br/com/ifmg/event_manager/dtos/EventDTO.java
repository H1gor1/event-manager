package br.com.ifmg.event_manager.dtos;

import br.com.ifmg.event_manager.entities.Event;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.util.Objects;

public class EventDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDate eventDate;
    private Integer guestNumber;
    private Long addressId; // ID do endereço associado ao evento
    private Long userId; // ID do usuário associado ao evento

    public EventDTO() {
    }

    public EventDTO(Long id, String name, String description, LocalDate eventDate, Integer guestNumber, Long addressId, Long userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.guestNumber = guestNumber;
        this.addressId = addressId;
        this.userId = userId;
    }

    public EventDTO(Event entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.eventDate = entity.getEventDate();
        this.guestNumber = entity.getGuestNumber();
        this.addressId = entity.getAddress().getId(); // Supondo que o método getAddressId() exista
        this.userId = entity.getUser().getId(); // Supondo que o método getUserId() exista
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(Integer guestNumber) {
        this.guestNumber = guestNumber;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventDTO eventDTO)) return false;
        return Objects.equals(id, eventDTO.id) && Objects.equals(name, eventDTO.name) && Objects.equals(description, eventDTO.description) && Objects.equals(eventDate, eventDTO.eventDate) && Objects.equals(guestNumber, eventDTO.guestNumber) && Objects.equals(addressId, eventDTO.addressId) && Objects.equals(userId, eventDTO.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, eventDate, guestNumber, addressId, userId);
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", guestNumber=" + guestNumber +
                ", addressId=" + addressId +
                ", userId=" + userId +
                '}';
    }
}
