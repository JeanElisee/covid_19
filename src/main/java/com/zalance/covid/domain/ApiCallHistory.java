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
    private Status status;
    private Date date;
    private ApiCallType apiCallType;

    public ApiCallHistory() {
    }

    public ApiCallHistory(Status status, Date date, ApiCallType apiCallType) {
    }
}
