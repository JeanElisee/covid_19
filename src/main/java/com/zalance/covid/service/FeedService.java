package com.zalance.covid.service;

import com.zalance.covid.constant.ApiCallType;

public interface FeedService {
    void getDataFromApi(ApiCallType apiCallType);

    void getCountryAndSave(ApiCallType from);
}
