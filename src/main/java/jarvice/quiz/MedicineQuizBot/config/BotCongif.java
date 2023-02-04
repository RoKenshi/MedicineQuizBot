package jarvice.quiz.MedicineQuizBot.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class BotCongif {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.key}")
    String token;
}
