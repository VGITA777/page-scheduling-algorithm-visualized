package com.prince.schedulingalgogui.utils;

public enum ViewsLocation {
    HOMEPAGE("/views/HomePage.fxml"),

    ;

    private final String location;

    ViewsLocation(String location) {
        this.location = location;
    }
}