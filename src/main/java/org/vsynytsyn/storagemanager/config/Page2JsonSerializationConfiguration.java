package org.vsynytsyn.storagemanager.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import java.io.IOException;

@Configuration
public class Page2JsonSerializationConfiguration {

    @Bean
    public Module springDataPageModule() {
        return new SimpleModule().addSerializer(Page.class, new JsonSerializer<Page>() {
            @Override
            public void serialize(
                    Page page, JsonGenerator jsonGen, SerializerProvider serializerProvider
            ) throws IOException {
                ObjectMapper om = new ObjectMapper().disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                                    .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
                jsonGen.writeStartObject();

                jsonGen.writeFieldName("content");
                jsonGen.writeRawValue(
                        om.writerWithView(serializerProvider.getActiveView()).writeValueAsString(page.getContent()));

                jsonGen.writeFieldName("pageable");
                jsonGen.writeRawValue(
                        om.writeValueAsString(page.getPageable()));

                jsonGen.writeFieldName("first");
                jsonGen.writeBoolean(page.isFirst());
                jsonGen.writeFieldName("last");
                jsonGen.writeBoolean(page.isLast());

                jsonGen.writeFieldName("totalElements");
                jsonGen.writeNumber(page.getTotalElements());
                jsonGen.writeFieldName("totalPages");
                jsonGen.writeNumber(page.getTotalPages());
                jsonGen.writeFieldName("sizePerPage");
                jsonGen.writeNumber(page.getSize());

                jsonGen.writeFieldName("currentPageNumber");
                jsonGen.writeNumber(page.getNumber());
                jsonGen.writeFieldName("empty");
                jsonGen.writeBoolean(page.isEmpty());
                jsonGen.writeFieldName("numberOfElementsOnCurrentPage");
                jsonGen.writeNumber(page.getNumberOfElements());

                jsonGen.writeObjectField("sort", page.getSort());

                jsonGen.writeEndObject();
            }
        });
    }
}
