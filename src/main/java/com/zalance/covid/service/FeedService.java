package com.zalance.covid.service;

import com.zalance.covid.constant.ApiCallType;

public interface FeedService {
    void getDataFromApi();

    void getCountryAndSave();
}
