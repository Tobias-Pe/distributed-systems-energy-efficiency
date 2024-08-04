package edu.hm.peslalz.thesis.userservice;

import edu.hm.peslalz.thesis.userservice.controller.UserAccountController;
import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserserviceApplicationTests {

    @MockBean
    FanoutExchange fanoutExchange;

    @MockBean
    RabbitTemplate rabbitTemplate;

    @Autowired
    private UserAccountController userAccountController;

    @Test
    @Transactional
    void scenario() {
        UserAccount testiasTestlalz = createUser();
        testiasTestlalz = updateUsername(testiasTestlalz);
        UserAccount contentCreator = createNewUserAndFollow(testiasTestlalz);
        countFollowers(contentCreator);
    }

    private UserAccount createUser() {
        UserAccount testiasTestlalz = userAccountController.createUserAccount(new UserAccountRequest("Testias Testlaz"));
        assertThat(testiasTestlalz.getId()).isNotNull();
        assertThat(userAccountController.getUserAccount(testiasTestlalz.getId())).isEqualTo(userAccountController.searchUser("Testias Testlaz", 0).stream().iterator().next());
        return testiasTestlalz;
    }

    private UserAccount updateUsername(UserAccount testiasTestlalz) {
        testiasTestlalz = userAccountController.updateUser(new UserAccountRequest("Testias Testlalz"), testiasTestlalz.getId());
        assertThat(testiasTestlalz.getUsername()).isEqualTo("Testias Testlalz");
        return testiasTestlalz;
    }

    private UserAccount createNewUserAndFollow(UserAccount testiasTestlalz) {
        UserAccount contentCreator = userAccountController.createUserAccount(new UserAccountRequest("ContentCreator"));
        testiasTestlalz = userAccountController.followUser(contentCreator.getUsername(), testiasTestlalz.getId());
        assertThat(testiasTestlalz.getFollowing()).hasSize(1);
        return contentCreator;
    }

    private void countFollowers(UserAccount contentCreator) {
        UserAccount contentCreatorLover = userAccountController.createUserAccount(new UserAccountRequest("ContentCreatorLover"));
        userAccountController.followUser(contentCreator.getUsername(), contentCreatorLover.getId());
        userAccountController.followUser(contentCreator.getUsername(), contentCreatorLover.getId());
        userAccountController.followUser(contentCreator.getUsername(), contentCreatorLover.getId());
        assertThat(userAccountController.getFollowers(contentCreator.getId())).hasSize(2);
    }
}
