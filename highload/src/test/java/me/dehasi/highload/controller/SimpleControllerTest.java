package me.dehasi.highload.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.web.bind.annotation.GetMapping;
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
@WebMvcTest(SimpleControllerTest.SimpleController.class)
public class SimpleControllerTest {

    @Autowired MockMvc mockMvc;

    MvcWrapper assertThat;

    @Before
    public void cresteWrapper() {
        assertThat = new MvcWrapper(mockMvc, new ObjectMapper());
    }

    @Test
    public void mvcTest() throws Exception {
        mockMvc.perform(
            post("/address/{param}", "lol")
                .contentType(APPLICATION_JSON_UTF8)
                .content("{\"field\":\"lol\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.field").value("lollol"));
    }

    @Test
    public void wraperTest() {
        assertThat
            .postToUrl("/address/{param}", "double")
            .withJsonBody(new Rsp("lol"))
            .hasStatusOk()
            .hasFieldWithValue("field", "doublelol")
            .hasBodyObjestEqualsTo(new Rsp("doublelol"));
    }

    static class MvcWrapper {
        private final MockMvc mockMvc;
        private final ObjectMapper objectMapper;
        private MockHttpServletRequestBuilder request;
        private ResultActions response;

        MvcWrapper(MockMvc mockMvc, ObjectMapper objectMapper) {
            this.mockMvc = Objects.requireNonNull(mockMvc);
            this.objectMapper = Objects.requireNonNull(objectMapper);
        }

        MvcWrapper postToUrl(String urlTemplate, Object... uriVars) {
            request = post(urlTemplate, uriVars);
            return this;
        }

        MvcWrapper withJsonBody(Object body) {
            try {
                request = request.contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsBytes(body));
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        MvcWrapper hasStatusOk() {
            perform();
            try {
                response.andExpect(status().isOk());
                return this;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        <T> MvcWrapper hasBodyObjestEqualsTo(T o) {
            perform();

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

        MvcWrapper hasFieldWithValue(String field, String fieldValue) {
            perform();
            try {
                response.andExpect(jsonPath("$." + field).value(fieldValue));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        private void perform() {
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

        @PostMapping("address/{param}") public Rsp post(@PathVariable("param") String param, @RequestBody Rsp rsp) {
            return new Rsp(param + rsp.field);
        }

        @GetMapping("lol") public String get() {
            return "lol";
        }
    }

    @Configuration
    @ComponentScan("me.dehasi.highload.controller")
    public static class Rsp {
        public final String field;

        @JsonCreator public Rsp(@JsonProperty("field") String field) {
            this.field = field;
        }

        public Rsp() {
            this.field = "31";
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Rsp))
                return false;
            Rsp rsp = (Rsp)o;
            return Objects.equals(field, rsp.field);
        }

        @Override public int hashCode() {
            return Objects.hash(field);
        }
    }
}
