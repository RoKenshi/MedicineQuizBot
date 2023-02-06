package jarvice.quiz.MedicineQuizBot.service;


import jarvice.quiz.MedicineQuizBot.config.BotCongif;
import jarvice.quiz.MedicineQuizBot.model.User;
import jarvice.quiz.MedicineQuizBot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {


    @Autowired
    private UserRepository userRepository;

    static final String HELP_TEXT = "This bot helps you learn English, click /start to memorize words";
    final BotCongif congif;

    public TelegramBot(BotCongif congif){
        this.congif = congif;
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start","get a welcome message"));
        botCommandList.add(new BotCommand("/mydata", "get your data stored"));
        botCommandList.add(new BotCommand("/deletedata","delete my data"));
        botCommandList.add(new BotCommand("/help","info how to use this bot"));
        botCommandList.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return congif.getBotName();
    }

    @Override
    public String getBotToken() {
        return congif.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":

                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId, "sorry, command was not recognized");



            }
        }
    }

    private void registerUser(Message msg) {
        if(userRepository.findById(msg.getChatId()).isEmpty()){
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(String.valueOf(new Timestamp(System.currentTimeMillis())));

            userRepository.save(user);
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "Hi, " + name + ", nice to meet you!";

        sendMessage(chatId, answer);
        log.info("Replied to user " + name);

    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();

        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred :" + e.getMessage());
        }
    }
}
