package com.rate.limiting.ratelimiting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;

@ContextConfiguration(classes = { RateLimitingController.class })
@ExtendWith(SpringExtension.class)
class RateLimitingControllerTest {

    @Autowired
    private RateLimitingController rateLimitingController;

    @Test
    void testGetItems() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/api/items").param("numTokens", "10");

        // expected
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(rateLimitingController)
                .build()
                .perform(requestBuilder);

        // assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @Test
    void testGetItemsThrowsTOO_MANY_REQUESTS() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/api/items").param("numTokens", "11");

        // expected
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(rateLimitingController)
                .build()
                .perform(requestBuilder);

        // assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(HttpStatus.TOO_MANY_REQUESTS.value()));
    }

    @Test
    void testGetItemsThrowsBadRequestWithZero() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/api/items").param("numTokens", "0");

        // expected
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(rateLimitingController)
                .build()
                .perform(requestBuilder);

        // assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testGetItemsThrowsBadRequestWithWrongData() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/api/items").param("numTokens", "token");

        // expected
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(rateLimitingController)
                .build()
                .perform(requestBuilder);

        // assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
    }
}
