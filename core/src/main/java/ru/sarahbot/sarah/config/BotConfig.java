package ru.sarahbot.sarah.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.sarahbot.sarah.service.EventProcessor;
import ru.utils.name.RandomNameService;

@Configuration
public class BotConfig {

    @Value("${jda.botkey}")
    private String token;

    @Value("${file.size.max:10000000}")
    private Integer maxFileSize;

    @Bean
    public JDA jda(EventProcessor eventProcessor) throws InterruptedException {
        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(eventProcessor)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("Hello"))
                .build()
                .awaitReady(); // waits until bot is ready

        jda.updateCommands().addCommands(Commands.slash("ping", "Calculate ping of the bot")).queue();

        return jda;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(conf -> conf.defaultCodecs().maxInMemorySize(maxFileSize))
                .build();
    }

    @Bean
    public RandomNameService randomNameService() {
        return new RandomNameService();
    }
}
