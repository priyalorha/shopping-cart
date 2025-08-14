package org.shoppingcart.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.shoppingcart.models.Fruit;
import org.shoppingcart.models.FruitConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@Component
public class FruitLoader {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Autowired
    public FruitLoader(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    public HashMap<String, Fruit> loadFruitConfig() throws IOException {
        FruitConfig fruitConfig;
        HashMap<String, Fruit> fruitHashMap = new HashMap<>(); // ✅ initialize before use

        Resource resource = resourceLoader.getResource("classpath:offer.json");
        try (InputStream inputStream = resource.getInputStream()) {
            fruitConfig = objectMapper.readValue(inputStream, FruitConfig.class);

            for (Fruit f : fruitConfig.getFruits()) {
                fruitHashMap.put(String.valueOf(f.getName()), f);
            }
        }

        return fruitHashMap;
    }

}
