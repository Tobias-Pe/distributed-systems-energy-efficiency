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
    void scenario() throws Exception {
        UserAccount testiasTestlalz = createUser();
        testiasTestlalz = updateUsername(testiasTestlalz);
        UserAccount contentCreator = createNewUserAndFollow(testiasTestlalz);
        countFollowers(contentCreator);
    }

    private UserAccount createUser() throws Exception {
        UserAccount testiasTestlalz = userAccountController.createUserAccount(new UserAccountRequest("Testias Testlaz")).call();
        assertThat(testiasTestlalz.getId()).isNotNull();
        assertThat(userAccountController.getUserAccount(testiasTestlalz.getId()).call()).isEqualTo(userAccountController.searchUser("Testias Testlaz", 0).call().stream().iterator().next());
        return testiasTestlalz;
    }

    private UserAccount updateUsername(UserAccount testiasTestlalz) throws Exception {
        testiasTestlalz = userAccountController.updateUser(new UserAccountRequest("Testias Testlalz"), testiasTestlalz.getId()).call();
        assertThat(testiasTestlalz.getUsername()).isEqualTo("Testias Testlalz");
        return testiasTestlalz;
    }

    private UserAccount createNewUserAndFollow(UserAccount testiasTestlalz) throws Exception {
        UserAccount contentCreator = userAccountController.createUserAccount(new UserAccountRequest("ContentCreator")).call();
        testiasTestlalz = userAccountController.followUser(contentCreator.getUsername(), testiasTestlalz.getId()).call();
        assertThat(testiasTestlalz.getFollowing()).hasSize(1);
        return contentCreator;
    }

    private void countFollowers(UserAccount contentCreator) throws Exception {
        UserAccount contentCreatorLover = userAccountController.createUserAccount(new UserAccountRequest("ContentCreatorLover")).call();
        userAccountController.followUser(contentCreator.getUsername(), contentCreatorLover.getId()).call();
        userAccountController.followUser(contentCreator.getUsername(), contentCreatorLover.getId()).call();
        userAccountController.followUser(contentCreator.getUsername(), contentCreatorLover.getId()).call();
        assertThat(userAccountController.getFollowers(contentCreator.getId()).call()).hasSize(2);
    }
}
