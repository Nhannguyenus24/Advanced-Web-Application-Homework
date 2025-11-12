@Configuration
public class RabbitConfig {
    @Value("${app.queue}")
    private String queueName;

    @Bean
    public Queue queue() {
        return new Queue(queueName, false);
    }
}
