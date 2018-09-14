package amcmurray.bw.storage;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import amcmurray.bw.storage.repositories.MentionRepository;
import amcmurray.bw.twitterdomainobjects.Mention;

public class MentionStorageService implements Runnable {

    private final static String TOPIC = "mentions";
    private static final Logger logger = LoggerFactory.getLogger(MentionStorageService.class);
    private final KafkaConsumer<String, Mention> kafkaConsumer;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    @Autowired
    private MentionRepository mentionRepository;

    @Autowired
    public MentionStorageService(KafkaConsumer<String, Mention> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

    @Override
    public void run() {
        this.kafkaConsumer.subscribe(Collections.singletonList(TOPIC));

        try {
            while (true) {
                ConsumerRecords<String, Mention> records = kafkaConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, Mention> record : records) {
                    mentionRepository.save(record.value());
                    logger.debug(record.key() + " saved.");
                }

                if (!records.isEmpty()) {
                    logger.debug("{} records found and saved.", records.count());
                }
            }
        } catch (WakeupException e) {
            if (!closed.get()) {
                throw e;
            }
        } finally {
            kafkaConsumer.close();
        }
    }

    public void shutdown() {
        closed.set(true);
        kafkaConsumer.wakeup();
    }

    public void start() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this);
    }
}

