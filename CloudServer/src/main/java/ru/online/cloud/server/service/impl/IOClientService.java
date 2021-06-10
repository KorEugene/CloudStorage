package ru.online.cloud.server.service.impl;

import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.ClientService;
import ru.online.cloud.server.service.CommandDictionaryService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class IOClientService implements ClientService {

    private Socket clientSocket;

    private DataInputStream in;
    private DataOutputStream out;

    private CommandDictionaryService dictionaryService;

    public IOClientService(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.dictionaryService = Factory.getCommandDirectoryService();

        initializeIOStreams();
    }

    private void initializeIOStreams() {
        try {
            this.in = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startIOProcess() {
        new Thread(() -> {
            try {
                while (true) {
                    String clientCommand = readCommand();
                    String commandResult = dictionaryService.processCommand(clientCommand);

                    writeCommandResult(commandResult);
                }
            } catch (Exception ex) {
                System.err.println("Client error: " + ex.getMessage());
            } finally {
                closeConnection();
            }

        }).start();
    }

    public void writeCommandResult(String commandResult) {
        try {
            out.writeUTF(commandResult);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readCommand() {
        try {
            return in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException("Read command result exception: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
