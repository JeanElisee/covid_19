package com.zalance.covid.domain;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.constant.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(schema = "covid_19_schema")
public class ApiCallHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;
    private String status;
    private Date date;
    private String apiCallType;
    @Column(name = "data_retrieved")
    private Long amountOfDataRetrieved;

    public ApiCallHistory() {
    }

    public ApiCallHistory(String status, Date date, String apiCallType) {
        this.status = status;
        this.date = date;
        this.apiCallType = apiCallType;
    }

    public ApiCallHistory(String status, Date date, String apiCallType, Long amountOfDataRetrieved) {
        this.status = status;
        this.date = date;
        this.apiCallType = apiCallType;
        this.amountOfDataRetrieved = amountOfDataRetrieved;
    }
}
