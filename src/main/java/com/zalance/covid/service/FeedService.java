package com.zalance.covid.service;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.exception.CovidException;
import com.zalance.covid.exception.RetryException;

public interface FeedService {
    void getDataFromApi(ApiCallType apiCallType) throws RetryException, CovidException;

    void getCountryAndSave(ApiCallType from) throws RetryException, CovidException;
}
