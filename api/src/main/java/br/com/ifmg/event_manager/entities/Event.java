
package br.com.ifmg.event_manager.entities;

import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDate eventDate;
    private Integer guestNumber; // numero de convidados total

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL)
    private Address address; // relacionamento com Address 1-1, um evento tem um endereço

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // relacionamento com User 1-N, um usuário pode ter mais de 1 evento e um evento pertence a um usuário

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TableEntity> tables = new ArrayList<>(); // relacionamento com TableEntity 1-N, um evento pode ter várias mesas

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
        if (address != null) {
            address.setEvent(this);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<TableEntity> getTables() {
        return tables;
    }

    public void setTables(List<TableEntity> tables) {
        this.tables = tables;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event event)) return false;
        return Objects.equals(id, event.id) && Objects.equals(name, event.name) && Objects.equals(description, event.description) && Objects.equals(eventDate, event.eventDate) && Objects.equals(guestNumber, event.guestNumber) && Objects.equals(user, event.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, eventDate, guestNumber, user);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", guestNumber=" + guestNumber +
                ", user=" + user +
                '}';
    }
}