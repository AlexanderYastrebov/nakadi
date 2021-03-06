package de.zalando.aruha.nakadi.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zalando.problem.ProblemModule;

import java.io.IOException;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Configuration
public class JsonConfig {

    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        objectMapper.registerModule(enumModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new ProblemModule());
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS , false);

        return objectMapper;
    }

    private SimpleModule enumModule() {
        // see http://stackoverflow.com/questions/24157817/jackson-databind-enum-case-insensitive
        final SimpleModule enumModule = new SimpleModule();
        enumModule.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<Enum> modifyEnumDeserializer(final DeserializationConfig config,
                                                                 final JavaType type,
                                                                 final BeanDescription beanDesc,
                                                                 final JsonDeserializer<?> deserializer) {
                return new LowerCaseEnumJsonDeserializer(type);
            }
        });
        enumModule.addSerializer(Enum.class, new LowerCaseEnumJsonSerializer());
        return enumModule;
    }

    private static class LowerCaseEnumJsonDeserializer extends JsonDeserializer<Enum> {
        private final JavaType type;

        public LowerCaseEnumJsonDeserializer(final JavaType type) {
            this.type = type;
        }

        @Override
        public Enum deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            @SuppressWarnings("unchecked")
            final Class<? extends Enum> rawClass = (Class<Enum<?>>) type.getRawClass();
            final String jpValueAsString = jp.getValueAsString();
            try {
                return Enum.valueOf(rawClass, jpValueAsString.toUpperCase());
            } catch (final IllegalArgumentException e) {
                final String possibleValues = stream(rawClass.getEnumConstants())
                        .map(enumValue -> enumValue.name().toLowerCase())
                        .collect(joining(", "));
                throw new JsonMappingException("Illegal enum value: '" + jpValueAsString + "'. Possible values: [" + possibleValues + "]");
            }
        }
    }

    private static class LowerCaseEnumJsonSerializer extends StdSerializer<Enum> {
        public LowerCaseEnumJsonSerializer() {
            super(Enum.class);
        }

        @Override
        public void serialize(final Enum value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
            jgen.writeString(value.name().toLowerCase());
        }
    }
}
