package de.antidrowsinessalarm.eventproducer;

public class ConfigFactory {

    public static DrowsyEventProducer.Config createDefaultConfig() {
        return DrowsyEventProducer.Config.builder()
                .setDrowsyThreshold(0.15)
                .setLikelyDrowsyThreshold(0.08)
                .build();
    }
}
