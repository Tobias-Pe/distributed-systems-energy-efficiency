package edu.hm.peslalz.thesis.notificationservice.client;

import edu.hm.peslalz.thesis.notificationservice.entity.UserMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("userservice")
public interface UserClient {
    @GetMapping("users/{id}/followers")
    ResponseEntity<List<UserMessage>> getUserFollowers(@PathVariable int id);
}
