package springbook.user;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

// 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
@RunWith(SpringJUnit4ClassRunner.class)
// 테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정
@ContextConfiguration(locations="/applicationContext.xml")
public class UserDaoTest {

        @Autowired
        // 테스트 오브젝트가 만들어지고 나면 스프링 테스트 컨텍스트에 의해 자동으로 값이 주입된다.
        private ApplicationContext context;

        @Autowired
        private UserDao dao;
        private User user1;
        private User user2;
        private User user3;


        @Before
        public void setUp(){

                System.out.println(this.context);
                System.out.println(this);

                this.dao = this.context.getBean("userDao",UserDao.class);

                this.user1 = new User("gyumee","박성철","springno1");
                this.user2 = new User("leegw700","이길원","springno2");
                this.user3 = new User("bumjin","박범진","springno3");
        }
        @Test
        public void addAndGet() throws ClassNotFoundException,SQLException{

                User user1 = new User("gyumee","박성철","springno1");
                User user2 = new User("leegw700","이길원","springno2");

                dao.deleteAll();
                assertThat(dao.getCount(),is(0));

                dao.add(user1);
                dao.add(user2);
                assertThat(dao.getCount(),is(2));

                User userget1 = dao.get(user1.getId());
                assertThat(userget1.getName(),is(user1.getName()));
                assertThat(userget1.getPassword(),is(user1.getPassword()));

                User userget2 = dao.get(user2.getId());
                assertThat(userget2.getName(),is(user2.getName()));
                assertThat(userget2.getPassword(),is(user2.getPassword()));
        }

        @Test
        public void count() throws ClassNotFoundException, SQLException{

                User user1 = new User("gyumee","박성철","springno1");
                User user2 = new User("leegw700","이길원","springno2");
                User user3 = new User("bumjin","박범진","springno3");

                dao.deleteAll();
                assertThat(dao.getCount(),is(0));

                dao.add(user1);
                assertThat(dao.getCount(),is(1));

                dao.add(user2);
                assertThat(dao.getCount(),is(2));

                dao.add(user3);
                assertThat(dao.getCount(),is(3));

        }

        @Test(expected= EmptyResultDataAccessException.class)
        public void getUserFailure() throws ClassNotFoundException ,SQLException{

                dao.deleteAll();
                assertThat(dao.getCount(),is(0));

                dao.get("unknown_id");
        }

}
