package edu.hm.peslalz.thesis.userservice;

import edu.hm.peslalz.thesis.userservice.controller.UserAccountController;
import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserserviceApplicationTests {

    @Autowired
    private UserAccountController userAccountController;

    @Test
    @Transactional
    void scenario() {
        UserAccount testiasTestlalz = userAccountController.createUserAccount(new UserAccountRequest("Testias Testlaz"));
        assertThat(testiasTestlalz.getId()).isNotNull();
        testiasTestlalz = userAccountController.updateUser(new UserAccountRequest("Testias Testlalz"), testiasTestlalz.getId());
        assertThat(testiasTestlalz.getUsername()).isEqualTo("Testias Testlalz");
        assertThat(userAccountController.getUserAccount(testiasTestlalz.getId())).isEqualTo(userAccountController.searchUser("Testias Testlalz").stream().iterator().next());
        UserAccount contentCreator = userAccountController.createUserAccount(new UserAccountRequest("ContentCreator"));
        testiasTestlalz=userAccountController.followUser(contentCreator.getUsername(), testiasTestlalz.getId());
        assertThat(testiasTestlalz.getFollowing()).hasSize(1);
        UserAccount contentCreatorLover = userAccountController.createUserAccount(new UserAccountRequest("ContentCreatorLover"));
        userAccountController.followUser(contentCreator.getUsername(), contentCreatorLover.getId());
        userAccountController.followUser(contentCreator.getUsername(), contentCreatorLover.getId());
        userAccountController.followUser(contentCreator.getUsername(), contentCreatorLover.getId());
        assertThat(userAccountController.getFollowers(contentCreator.getId())).hasSize(2);
    }

}
