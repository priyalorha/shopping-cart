package org.shoppingcart.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BOGO extends OfferDetails {
     @JsonProperty("buy")
     private Integer buy;
     @JsonProperty("free")
     private Integer free;

     public String toString() {
          return String.format(
                  "{\"buy\": \"%s\", \"free\": %s}",
                  buy,
                  free
          );
     }
}
