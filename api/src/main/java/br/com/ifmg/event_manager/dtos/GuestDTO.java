package br.com.ifmg.event_manager.dtos;

import br.com.ifmg.event_manager.entities.Guest;

import java.util.Objects;

public class GuestDTO {
    private Long id;
    private String name;
    private String email;
    private Boolean confirmed;
    private Long tableId; // Adicionando o ID da mesa

    public GuestDTO() {
    }

    public GuestDTO(Long id, String name, String email, Boolean confirmed, Long tableId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.confirmed = confirmed;
        this.tableId = tableId;
    }

    public GuestDTO(Guest guest) {
        this.id = guest.getId();
        this.name = guest.getName();
        this.email = guest.getEmail();
        this.confirmed = guest.getConfirmed();
        this.tableId = guest.getTable() != null ? guest.getTable().getId() : null;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GuestDTO guestDTO)) return false;
        return Objects.equals(id, guestDTO.id) && Objects.equals(name, guestDTO.name) &&
                Objects.equals(email, guestDTO.email) && Objects.equals(confirmed, guestDTO.confirmed) &&
                Objects.equals(tableId, guestDTO.tableId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, confirmed, tableId);
    }

    @Override
    public String toString() {
        return "GuestDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", confirmed=" + confirmed +
                ", tableId=" + tableId +
                '}';
    }
}