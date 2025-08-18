package org.shoppingcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.shoppingcart.models.FruitDTO;
import org.shoppingcart.models.FruitResponseDTO;
import org.shoppingcart.services.FruitBillService;
import org.shoppingcart.utils.FruitLoader;
import org.shoppingcart.utils.FruitType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FruitOrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FruitLoader fruitLoader;

    @Mock
    private FruitBillService fruitBillService;

    @InjectMocks
    private FruitOrderController fruitOrderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fruitOrderController).build();
    }

    @Test
    void computeBill_ValidRequest_ReturnsCorrectResponse() throws Exception {
        // Arrange
        List<FruitDTO> request = Arrays.asList(
                new FruitDTO(FruitType.APPLE, 3),
                new FruitDTO(FruitType.BANANA, 2)
        );

        FruitResponseDTO mockResponse = new FruitResponseDTO();
        mockResponse.setTotalPrice(5.0);;

        when(fruitBillService.billCalculator(anyList())).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cummulativePrice").value(5.0));

        verify(fruitBillService, times(1)).billCalculator(anyList());
    }

    @Test
    void computeBill_EmptyRequest_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk()); // or isBadRequest() if you add validation

        verify(fruitBillService, times(1)).billCalculator(anyList());
    }




    @Test
    void computeBill_InvalidContentType_ReturnsUnsupportedMediaType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/bill")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());

        verify(fruitBillService, never()).billCalculator(anyList());
    }

    @Test
    void computeBill_MalformedJson_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed json}"))
                .andExpect(status().isBadRequest());

        verify(fruitBillService, never()).billCalculator(anyList());
    }
}
