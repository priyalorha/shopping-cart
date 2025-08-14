package org.shoppingcart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.shoppingcart.models.FruitDTO;
import org.shoppingcart.models.FruitResponseDTO;
import org.shoppingcart.services.FruitBillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FruitOrderController {

    private static final Logger log = LoggerFactory.getLogger(FruitOrderController.class);

    private final FruitBillService fruitCalculatorService;

    @Autowired
    public FruitOrderController(FruitBillService fruitCalculatorService) {
        this.fruitCalculatorService = fruitCalculatorService;
    }

    @PostMapping("/bill")
    public ResponseEntity<FruitResponseDTO> computeBill(
            @Valid @NotEmpty(message = "Fruit list cannot be empty")
            @RequestBody List<FruitDTO> fruitDTO) throws IOException {

        log.info("Received bill request for {} items", fruitDTO.size());
        FruitResponseDTO fruitBill = fruitCalculatorService.billCalculator(fruitDTO);
        log.info("Bill computed successfully: {}", fruitBill);

        return ResponseEntity.status(HttpStatus.OK).body(fruitBill);
    }
}