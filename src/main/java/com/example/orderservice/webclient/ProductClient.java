package com.example.orderservice.webclient;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.orderservice.dtos.ProductDto;

@Service
public class ProductClient {

	private final WebClient webClient;

	public ProductClient(WebClient.Builder builder) {
		this.webClient = builder.baseUrl("http://localhost:8082/api/products").build();
	}

	public ProductDto getProductById(Long productId) {
		return webClient.get().uri("/{id}", productId).retrieve().bodyToMono(ProductDto.class).block();
	}

	public boolean isAvailable(Long productId, Long quantity) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/{id}/available").queryParam("quantity", quantity).build(productId))
				.retrieve().bodyToMono(Boolean.class).block();
	}

	public boolean reduceStock(Long productId, Long quantity) {
		return webClient.put()
				.uri(uriBuilder -> uriBuilder.path("/{id}/reduce-stock").queryParam("quantity", quantity)
						.build(productId))
				.retrieve().toBodilessEntity() // converts response to ResponseEntity<Void>
				.map(response -> response.getStatusCode().is2xxSuccessful()) 
				.block();
	}
	
	
	public boolean restoreStock(Long productId, Long quantity) {
		return webClient.put()
				.uri(uriBuilder -> uriBuilder.path("/{id}/restore-stock").queryParam("quantity", quantity)
						.build(productId))
				.retrieve().toBodilessEntity() // converts response to ResponseEntity<Void>
				.map(response -> response.getStatusCode().is2xxSuccessful()) 
				.block();
	}

}
