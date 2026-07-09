package com.example.oficina.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "veiculo")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String marca;

    @Column(length = 150, nullable = false)
    private String modelo;

    @Column(nullable = false)
    private Integer ano;

    @JoinColumn(name = "dono_id")
    @ManyToOne
    private Dono dono;

}
