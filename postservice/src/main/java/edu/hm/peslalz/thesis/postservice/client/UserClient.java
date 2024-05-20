package edu.hm.peslalz.thesis.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("userservice")
public interface UserClient {
    @GetMapping("users/{id}")
    ResponseEntity<Object> getUserAccount(@PathVariable int id);
}
