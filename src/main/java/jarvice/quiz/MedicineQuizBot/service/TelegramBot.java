package jarvice.quiz.MedicineQuizBot.service;


import jarvice.quiz.MedicineQuizBot.config.BotCongif;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotCongif congif;

    public TelegramBot(BotCongif congif){
        this.congif = congif;
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
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:


            }
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
