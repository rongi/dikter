package com.example.test.transform;

import com.example.test.domain.CommentRest;
import com.example.test.domain.ImageRest;
import com.example.test.domain.StatusRest;
import com.example.test.domain.SubscriberRest;
import com.example.test.domain.UserRest;
import com.example.test.entity.Comment;
import com.example.test.entity.Image;
import com.example.test.entity.Status;
import com.example.test.entity.Subscriber;
import com.example.test.entity.User;
import java.util.ArrayList;
import java.util.List;

public abstract class UserTransform {
    public static User toEntity(UserRest rest) {
        if (rest == null) return null;
        User entity = new User();

        entity.setName(rest.name);
        entity.setAge(rest.age);
        entity.setKawaii(rest.kawaii);
        entity.setHeight(rest.height);
        entity.setSubscribers(subscribersToEntity(rest.subscribers));
        entity.setStatus(statusToEntity(rest.status));
        entity.setComments(commentsToEntity(rest.comments));
        entity.setImage(imageToEntity(rest.image));

        return entity;
    }

    public static UserRest toRest(User entity) {
        if (entity == null) return null;
        UserRest rest = new UserRest();

        rest.name = entity.getName();
        rest.age = entity.getAge();
        rest.kawaii = entity.getKawaii();
        rest.height = entity.getHeight();
        rest.subscribers = subscribersToRest(entity.getSubscribers());
        rest.status = statusToRest(entity.getStatus());
        rest.comments = commentsToRest(entity.getComments());
        rest.image = imageToRest(entity.getImage());

        return rest;
    }

    private static List<Subscriber> subscribersToEntity(List<SubscriberRest> rests) {
        List<Subscriber> entities = new ArrayList<>();

        if (rests != null) {
            for (SubscriberRest rest : rests) {
                Subscriber entity = SubscriberTransform.toEntity(rest);
                entities.add(entity);
            }
        }

        return entities;
    }

    private static Status statusToEntity(StatusRest rest) {
        if (rest == null) return null;
        return StatusTransform.toEntity(rest);
    }

    private static List<Comment> commentsToEntity(List<CommentRest> rests) {
        List<Comment> entities = new ArrayList<>();

        if (rests != null) {
            for (CommentRest rest : rests) {
                Comment entity = CommentTransform.toEntity(rest);
                entities.add(entity);
            }
        }

        return entities;
    }

    private static Image imageToEntity(ImageRest rest) {
        if (rest == null) return null;
        return ImageTransform.toEntity(rest);
    }

    private static List<SubscriberRest> subscribersToRest(List<Subscriber> entities) {
        List<SubscriberRest> rests = new ArrayList<>();

        if (entities != null) {
            for (Subscriber entity : entities) {
                SubscriberRest rest = SubscriberTransform.toRest(entity);
                rests.add(rest);
            }
        }

        return rests;
    }

    private static StatusRest statusToRest(Status entity) {
        if (entity == null) return null;
        return StatusTransform.toRest(entity);
    }

    private static List<CommentRest> commentsToRest(List<Comment> entities) {
        List<CommentRest> rests = new ArrayList<>();

        if (entities != null) {
            for (Comment entity : entities) {
                CommentRest rest = CommentTransform.toRest(entity);
                rests.add(rest);
            }
        }

        return rests;
    }

    private static ImageRest imageToRest(Image entity) {
        if (entity == null) return null;
        return ImageTransform.toRest(entity);
    }
}