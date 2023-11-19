package springbook.user.service;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.exception.TestUserServiceException;
import springbook.user.dao.UserDao;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;


// 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
@RunWith(SpringJUnit4ClassRunner.class)
// 테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest extends TestCase {
    @Autowired
    UserService userService;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    UserDao userDao = new UserDaoJdbc();

    @Autowired
    MailSender mailSender;

    List<User> users;

    public static int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static int MIN_RECCOMENDED_FOR_GOLD = 30;

    @Before
    public void setUp(){
        users = Arrays.asList(
                new User("bumjin","박범진","p1", Level.BASIC,MIN_LOGCOUNT_FOR_SILVER-1,0,"bumjin@gmail.com"),
                new User("joytouch","강명성","p2",Level.BASIC,MIN_LOGCOUNT_FOR_SILVER,0,"joytouch@gmail.com"),
                new User("erwins","신승환","p3",Level.SILVER,60,MIN_RECCOMENDED_FOR_GOLD-1,"erwins@gmail.com"),
                new User("madnite1","이상호","p4",Level.SILVER,60,MIN_RECCOMENDED_FOR_GOLD,"madnite1@gmail.com"),
                new User("green","오민규","p5",Level.GOLD,100,Integer.MAX_VALUE,"green@gmail.com")
        );
    }

    @Test
    public void bean(){
        assertThat(this.userService,is(notNullValue()));
    }

    @Test
    // 컨텍스트 DI 설정을 변경하는 테스트라는것을 의미
    @DirtiesContext
    public void upgradeLevels() throws Exception{
        userDao.deleteAll();

        for(User user : users) userDao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        userService.setMailSender(mockMailSender);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0),false);
        checkLevelUpgraded(users.get(1),true);
        checkLevelUpgraded(users.get(2),false);
        checkLevelUpgraded(users.get(3),true);
        checkLevelUpgraded(users.get(4),false);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(),is(2));
        assertThat(request.get(0),is(users.get(1).getEmail()));
        assertThat(request.get(1),is(users.get(3).getEmail()));

    }

    private void checkLevelUpgraded(User user,boolean upgraded){
        User userUpdate = userDao.get(user.getId());
        if(upgraded){
            assertThat(userUpdate.getLevel(),is(user.getLevel().nextLevel()));
        }else{
            assertThat(userUpdate.getLevel(),is(user.getLevel()));
        }
    }

    @Test
    public void add(){
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(),is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(),is(userWithoutLevel.getLevel()));
    }

    @Test
    public void upgradeAllOrNothing() throws Exception{
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setTransactionManager(transactionManager);
        testUserService.setMailSender(mailSender);
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try{
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        }catch(TestUserServiceException e){
            e.printStackTrace();
        }
        checkLevelUpgraded(users.get(1),false);
    }

}