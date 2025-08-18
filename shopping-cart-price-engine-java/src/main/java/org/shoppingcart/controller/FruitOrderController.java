package org.shoppingcart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.shoppingcart.models.Fruit;
import org.shoppingcart.models.FruitDTO;
import org.shoppingcart.models.FruitResponseDTO;
import org.shoppingcart.services.FruitBillService;
import org.shoppingcart.utils.FruitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            @RequestBody List<FruitType> fruits) throws IOException {


        Map<FruitType, Long> fruitCount = fruits.stream()
                .collect(Collectors.groupingBy(f -> f, Collectors.counting()));

        // Convert to List<FruitDTO>
        List<FruitDTO> fruitDTOList = fruitCount.entrySet().stream()
                .map(entry -> new FruitDTO(entry.getKey(), entry.getValue().intValue()))
                .toList();


        List<FruitDTO> fruitDTO = new ArrayList<>();

        log.info("Received bill request for {} items", fruitDTOList.size());
        FruitResponseDTO fruitBill = fruitCalculatorService.billCalculator(fruitDTOList);
        log.info("Bill computed successfully: {}", fruitBill);

        return ResponseEntity.status(HttpStatus.OK).body(fruitBill);
    }
}