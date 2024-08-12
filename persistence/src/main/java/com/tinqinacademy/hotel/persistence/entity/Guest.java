package com.tinqinacademy.hotel.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

@Entity
@Table(name = "guests")
public class Guest extends BaseEntity {

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "phone_no",nullable = true)
    private String phoneNo;

    @Column(name = "id_card_validity",nullable = true)
    private LocalDate idCardValidity;

    @Column(name = "id_card_issue_authority",nullable = true)
    private String idCardIssueAuthority;

    @Column(name = "id_card_no",nullable = true,unique = true)
    private String idCardNo;

    @Column(name = "id_card_issue_date",nullable = true)
    private LocalDate idCardIssueDate;

    @Column(name = "birth_date",nullable = false)
    private LocalDate birthDate;
}
