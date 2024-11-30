package com.prince.schedulingalgogui.utils;

import lombok.Getter;
import lombok.NonNull;

@Getter
public enum ViewsLocation {
    HOME_PAGE("views/homePage/HomePage.fxml"),
    LOGIN_PAGE("views/loginPage/LoginPage.fxml"),
    ;

    @NonNull
    private final String location;

    ViewsLocation(String location) {
        this.location = location;
    }
}