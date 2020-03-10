package com.playground.loyalitypointsservice;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class UserLoyaltyDto {
    private String id;
    private String href;
    @JsonInclude(NON_NULL)
    private Long points;
    @JsonInclude(NON_NULL)
    private Long pendingPoints;

    UserLoyaltyDto() {
    }

    public String getid() {
        return id;
    }

    public void setUuid(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getPendingPoints() {
        return pendingPoints;
    }

    public void setPendingPoints(Long pendingPoints) {
        this.pendingPoints = pendingPoints;
    }
}
