package com.zalance.covid.constant;

import static org.junit.Assert.*;

import org.junit.Test;

public class NotificationCategoryTest {
    @Test
    public void test() {
        assertEquals("RESET_PASSWORD", NotificationCategory.RESET_PASSWORD.name());
        assertEquals("WELCOME", NotificationCategory.WELCOME.name());
    }

}
