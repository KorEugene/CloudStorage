package ru.online.cloud.client.model;

public enum FileType {

    FILE("F"), DIRECTORY("D");

    private final String name;

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
