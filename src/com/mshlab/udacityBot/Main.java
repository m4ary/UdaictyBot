package com.mshlab.udacityBot;

import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.mshlab.udacityBot.Consts.BOT_NAME;
import static com.mshlab.udacityBot.Consts.BOT_TOKEN;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("src/log4j.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(props);
        ApiContextInitializer.init();


        // Register our bot
        try {
            new TelegramBotsApi().registerBot(new Bot(BOT_TOKEN, BOT_NAME));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

    }
}
