package br.com.ifmg.event_manager.dtos;

import br.com.ifmg.event_manager.entities.TableEntity;

import java.util.Objects;

public class TableEntityDTO {

    private Long id;
    private String description;
    private Integer guestNumber;
    private Long eventId;

    public TableEntityDTO() {
    }

    public TableEntityDTO(Long id, String description, Integer guestNumber, Long eventId) {
        this.id = id;
        this.description = description;
        this.guestNumber = guestNumber;
        this.eventId = eventId;
    }

    public TableEntityDTO(TableEntity tableEntity) {
        this.id = tableEntity.getId();
        this.description = tableEntity.getDescription();
        this.guestNumber = tableEntity.getGuestNumber();
        this.eventId = tableEntity.getEvent() != null ? tableEntity.getEvent().getId() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(Integer guestNumber) {
        this.guestNumber = guestNumber;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TableEntityDTO that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(description, that.description) &&
                Objects.equals(guestNumber, that.guestNumber) && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, guestNumber, eventId);
    }

    @Override
    public String toString() {
        return "TableEntityDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", guestNumber=" + guestNumber +
                ", eventId=" + eventId +
                '}';
    }
}