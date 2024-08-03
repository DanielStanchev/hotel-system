package com.tinqinacademy.hotel.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

@Entity
@Table(name = "rooms")
public class Room extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "bathroom_type", nullable = false)
    private BathroomType bathroomType;

    @Column(name = "floor",nullable = false)
    private Integer floor;

    @Column(name = "room_no",nullable = false,unique = true)
    private String roomNo;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    private List<Bed> beds = new ArrayList<>();



    @JsonIgnore
    public void setBathroomTypeNotNull(BathroomType bathroomType) {
        if(bathroomType == null){
            return;
        }
        this.bathroomType = bathroomType;
    }

    @JsonIgnore
    public void setFloorNotNull(Integer floor) {
        if(floor == null){
            return;
        }
        this.floor = floor;
    }

    @JsonIgnore
    public void setRoomNoNotNull(String roomNo) {
        if(roomNo == null){
            return;
        }
        this.roomNo = roomNo;
    }

    @JsonIgnore
    public void setPriceNotNull(BigDecimal price) {
        if(price ==null){
            return;
        }
        this.price = price;
    }

    @JsonIgnore
    public void setBedsNotNull(List<Bed> beds) {
        if(beds.isEmpty()){
            return;
        }
        this.beds = beds;
    }
}
