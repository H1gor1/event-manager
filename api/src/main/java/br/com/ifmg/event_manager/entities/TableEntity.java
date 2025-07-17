package br.com.ifmg.event_manager.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_table_entity")
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer guestNumber; // numero de convidados nesta mesa

    @OneToMany(mappedBy = "table", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Guest> guests = new ArrayList<>(); // relacionamento com Guest 1-N, uma mesa pode ter vários convidados

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event; // relacionamento com Event N-1, várias mesas pertencem a um evento

    public TableEntity() {
    }

    public TableEntity(Long id, String description, Integer guestNumber, Event event) {
        this.id = id;
        this.description = description;
        this.guestNumber = guestNumber;
        this.event = event;
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

    public List<Guest> getGuests() {
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TableEntity that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(description, that.description) &&
                Objects.equals(guestNumber, that.guestNumber) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, guestNumber, event);
    }

    @Override
    public String toString() {
        return "TableEntity{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", guestNumber=" + guestNumber +
                ", event=" + event +
                '}';
    }
}