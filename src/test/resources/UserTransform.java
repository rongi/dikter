package com.example.test.transform;

import com.example.test.domain.StatusRest;
import com.example.test.domain.SubscriberRest;
import com.example.test.domain.UserRest;
import com.example.test.entity.Status;
import com.example.test.entity.Subscriber;
import com.example.test.entity.User;
import java.util.ArrayList;
import java.util.List;

public final class UserTransform {
    public static User fromRest(UserRest rest) {
        if (rest == null) return null;
        User entity = new User();

        entity.setName(rest.name);
        entity.setAge(rest.age);
        entity.setKawaii(rest.kawaii);
        entity.setHeight(rest.height);
        entity.setSubscribers(subscribersFromRest(rest.subscribers));
        entity.setStatus(statusFromRest(rest.status));

        return entity;
    }

    public static UserRest fromEntity(User entity) {
        if (entity == null) return null;
        UserRest rest = new UserRest();

        rest.name = entity.getName();
        rest.age = entity.getAge();
        rest.kawaii = entity.getKawaii();
        rest.height = entity.getHeight();
        rest.subscribers = subscribersFromEntity(entity.getSubscribers());
        rest.status = statusFromEntity(entity.getStatus());

        return rest;
    }

    private static List<Subscriber> subscribersFromRest(List<SubscriberRest> rests) {
        List<Subscriber> entities = new ArrayList<>();

        if (rests != null) {
            for (SubscriberRest rest : rests) {
                Subscriber entity = SubscriberTransform.fromRest(rest);
                entities.add(entity);
            }
        }

        return entities;
    }

    private static Status statusFromRest(StatusRest rest) {
        if (rest == null) return null;
        return StatusTransform.fromRest(rest);
    }

    private static List<SubscriberRest> subscribersFromEntity(List<Subscriber> entities) {
        List<SubscriberRest> rests = new ArrayList<>();

        if (entities != null) {
            for (Subscriber entity : entities) {
                SubscriberRest rest = SubscriberTransform.fromEntity(entity);
                rests.add(rest);
            }
        }

        return rests;
    }

    private static StatusRest statusFromEntity(Status entity) {
        if (entity == null) return null;
        return StatusTransform.fromEntity(entity);
    }
}