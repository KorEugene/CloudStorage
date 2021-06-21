package ru.online.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Command implements Serializable {

//    private String commandName;
    private CommandType commandName;
    private Object[] args;
}
