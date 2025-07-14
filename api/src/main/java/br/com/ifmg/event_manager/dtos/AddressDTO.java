package br.com.ifmg.event_manager.dtos;

import br.com.ifmg.event_manager.entities.Address;

import java.util.Objects;

public class AddressDTO {

    private Long id;
    private String street;
    private Integer number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private Long zipCode;

    public AddressDTO() {
    }

    public AddressDTO(Long id, String street, Integer number, String complement, String neighborhood, String city, String state, Long zipCode) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public AddressDTO(Address entity) {
        this.id = entity.getId();
        this.street = entity.getStreet();
        this.number = entity.getNumber();
        this.complement = entity.getComplement();
        this.neighborhood = entity.getNeighborhood();
        this.city = entity.getCity();
        this.state = entity.getState();
        this.zipCode = entity.getZipCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getZipCode() {
        return zipCode;
    }

    public void setZipCode(Long zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AddressDTO that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(street, that.street) && Objects.equals(number, that.number) && Objects.equals(complement, that.complement) && Objects.equals(neighborhood, that.neighborhood) && Objects.equals(city, that.city) && Objects.equals(state, that.state) && Objects.equals(zipCode, that.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, number, complement, neighborhood, city, state, zipCode);
    }

    @Override
    public String toString() {
        return "AddressDTO{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", number=" + number +
                ", complement='" + complement + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode=" + zipCode +
                '}';
    }
}
