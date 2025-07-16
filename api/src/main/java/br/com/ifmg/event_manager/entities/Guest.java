package br.com.ifmg.event_manager.entities;

import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "tb_guest")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String urlPhoto;
    private Boolean confirmed;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableEntity table; // relacionamento com TableEntity N-1, vários convidados podem pertencer a uma mesa

    public Guest() {
    }

    public Guest(Long id, String name, String email, String phone, String urlPhoto, Boolean confirmed, TableEntity table) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.urlPhoto = urlPhoto;
        this.confirmed = confirmed;
        this.table = table;
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

    public TableEntity getTable() {
        return table;
    }

    public void setTable(TableEntity table) {
        this.table = table;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Guest guest)) return false;
        return Objects.equals(id, guest.id) && Objects.equals(name, guest.name) &&
                Objects.equals(email, guest.email) && Objects.equals(confirmed, guest.confirmed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, confirmed);
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", confirmed=" + confirmed +
                '}';
    }
}