package com.example.medicalcrm.bot.cache;

import com.example.medicalcrm.entity.Application;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeletedApplicationCache {

    private final Map<Long, Application> deletedApplications = new ConcurrentHashMap<>();

    public Map<Long, Application> getDeletedApplications() {
        return deletedApplications;
    }
}