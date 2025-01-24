package com.icodesoft.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "`Role`")
public class Role {

    @Id
    private int id;

    private String name;

    private String describe;
}
