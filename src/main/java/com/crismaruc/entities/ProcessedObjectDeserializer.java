package com.crismaruc.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class ProcessedObjectDeserializer implements Deserializer {
    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public ProcessedObject deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        ProcessedObject processedObject = null;
        try {
            processedObject = mapper.readValue(bytes, ProcessedObject.class);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return processedObject;
    }

    @Override
    public void close() {

    }
}
