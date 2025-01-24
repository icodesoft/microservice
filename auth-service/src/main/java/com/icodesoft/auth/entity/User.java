package com.icodesoft.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name = "`User`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String password;

    private String email;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "`User_Role`",  // 中间表名称
    joinColumns = @JoinColumn(name = "user_id"),  // 本表外键
    inverseJoinColumns = @JoinColumn(name = "role_id"))  // 关联表外键
    private List<Role> roles;
}
