package com.zalance.covid.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(schema = "covid_19_schema")
public class GlobalCases {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;
    private Long newConfirmed;
    private Long totalConfirmed;
    private Long newDeaths;
    private Long totalDeaths;
    private Long newRecovered;
    private Long totalRecovered;

    private boolean isGlobal;

    @ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = CascadeType.MERGE)
    @JoinColumn(name = "country_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Country country;
}
