package me.dehasi.highload.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Created by Ravil on 13/02/2019. */

@RunWith(SpringRunner.class)
@WebMvcTest(SimpleControllerTest2.SimpleController.class)
public class SimpleControllerTest2 {

    @Autowired MockMvc mockMvc;

    Оболочка проверитьЧто;

    @Before
    public void cresteWrapper() {
        проверитьЧто = new Оболочка(mockMvc, new ObjectMapper());
    }

    @Test
    public void mvcTest() throws Exception {
        mockMvc.perform(
            post("/адрес/{param}", "lol")
                .contentType(APPLICATION_JSON_UTF8)
                .content("{\"поле\":\"lol\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.поле").value("lollol"));
    }

    @Test
    public void wraperTest() {
        проверитьЧто
            .посылкаНаАдрес("/адрес/{param}", "парам")
            .сСодержимым(new Rsp("-пам-пам"))
            .дошла()
            .ответСодержитПоле("поле", "парам-пам-пам")
            .содержимоеРавно(new Rsp("парам-пам-пам"));
    }

    static class Оболочка {
        private final MockMvc mockMvc;
        private final ObjectMapper objectMapper;
        private MockHttpServletRequestBuilder request;
        private ResultActions response;

        Оболочка(MockMvc mockMvc, ObjectMapper objectMapper) {
            this.mockMvc = Objects.requireNonNull(mockMvc);
            this.objectMapper = Objects.requireNonNull(objectMapper);
        }

        Оболочка посылкаНаАдрес(String urlTemplate, Object... uriVars) {
            request = post(urlTemplate, uriVars);
            return this;
        }

        Оболочка сСодержимым(Object body) {
            try {
                request = request.contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsBytes(body));
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        Оболочка дошла() {
            бахнуть();
            try {
                response.andExpect(status().isOk());
                return this;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        <T> Оболочка содержимоеРавно(T o) {
            бахнуть();

            byte[] array = response.andReturn().getResponse().getContentAsByteArray();
            try {
                T t = (T)objectMapper.readValue(array, o.getClass());
                assert t.equals(o);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        Оболочка ответСодержитПоле(String field, String fieldValue) {
            бахнуть();
            try {
                response.andExpect(jsonPath("$." + field).value(fieldValue));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        private void бахнуть() {
            if (response == null) {
                try {
                    response = mockMvc.perform(request);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @RestController
    public static class SimpleController {

        @PostMapping("адрес/{param}") public Rsp post(@PathVariable("param") String param, @RequestBody Rsp rsp) {
            return new Rsp(param + rsp.поле);
        }
    }

    @Configuration
    @ComponentScan("me.dehasi.highload.controller")
    public static class Rsp {
        public final String поле;

        @JsonCreator public Rsp(@JsonProperty("поле") String поле) {
            this.поле = поле;
        }

        public Rsp() {
            поле = "31";
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Rsp))
                return false;
            Rsp rsp = (Rsp)o;
            return Objects.equals(поле, rsp.поле);
        }

        @Override public int hashCode() {
            return Objects.hash(поле);
        }
    }
}
