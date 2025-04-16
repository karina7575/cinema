package com.javaacademy.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {
    private Integer id;
    private Session session;
    private Place place;
    private Boolean isBuy;
}
