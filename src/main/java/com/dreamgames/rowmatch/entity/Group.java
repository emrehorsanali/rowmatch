package com.dreamgames.rowmatch.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tournament_groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer maxLevel;
    private Boolean isFull;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_id", referencedColumnName = "id")
    private Tournament tournament;
    private Integer size;

    public Group(Integer maxLevel, Tournament tournament) {
        this.maxLevel = maxLevel;
        this.isFull = false;
        this.tournament = tournament;
        this.size = 0;
    }
}
