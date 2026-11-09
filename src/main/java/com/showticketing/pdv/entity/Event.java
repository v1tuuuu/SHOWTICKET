package com.showticketing.pdv.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.showticketing.pdv.enums.EventStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private String name;

    private String artist;
    private String venueName;
    private String city;
    private String state;
    private String address;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    private LocalDateTime doorsOpen;
    private Integer ageRating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.ACTIVE;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Sector> sectors = new ArrayList<>();

    public Event() {}

    public Event(String eventId, String name, String artist, String venueName, String city, String state, String address, LocalDateTime dateTime, LocalDateTime doorsOpen, Integer ageRating, EventStatus status) {
        this.eventId = eventId;
        this.name = name;
        this.artist = artist;
        this.venueName = venueName;
        this.city = city;
        this.state = state;
        this.address = address;
        this.dateTime = dateTime;
        this.doorsOpen = doorsOpen;
        this.ageRating = ageRating;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public LocalDateTime getDoorsOpen() { return doorsOpen; }
    public void setDoorsOpen(LocalDateTime doorsOpen) { this.doorsOpen = doorsOpen; }

    public Integer getAgeRating() { return ageRating; }
    public void setAgeRating(Integer ageRating) { this.ageRating = ageRating; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public List<Sector> getSectors() { return sectors; }
    public void setSectors(List<Sector> sectors) { this.sectors = sectors; }

    public void addSector(Sector sector) {
        sectors.add(sector);
        sector.setEvent(this);
    }
}
