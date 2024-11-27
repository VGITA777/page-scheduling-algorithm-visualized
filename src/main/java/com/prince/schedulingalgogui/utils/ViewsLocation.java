package com.prince.schedulingalgogui.utils;

import lombok.Getter;
import lombok.NonNull;

@Getter
public enum ViewsLocation {
    HOMEPAGE("views/homePage/HomePage.fxml"),
    LOGIN("views/loginPage/LoginPage.fxml"),
    ;

    @NonNull
    private final String location;

    ViewsLocation(String location) {
        this.location = location;
    }
}