package springbook.user;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

import javax.sql.DataSource;

import static org.junit.Assert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

// 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
@RunWith(SpringJUnit4ClassRunner.class)
// 테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정
@ContextConfiguration(locations="/test-applicationContext.xml")
// 테스트 메소드에서 애프리케이션 컨텍스트의 구성이나 상태를 변경한다는 것을 테스트 컨텍스트 프레임워크에 알려준다.
//@DirtiesContext
public class UserDaoTest {

        @Autowired
        private UserDao dao;
        @Autowired
        private DataSource dataSource;

        private User user1;
        private User user2;
        private User user3;


        @Before
        public void setUp(){
            this.user1 = new User("gyumee","�ڼ�ö","springno1");
            this.user2 = new User("leegw700","�̱��","springno2");
            this.user3 = new User("bumjin","�ڹ���","springno3");
        }
        @Test
        public void addAndGet(){

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
        public void count() {

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
        public void getUserFailure() {
            dao.deleteAll();
            assertThat(dao.getCount(),is(0));

            dao.get("unknown_id");
        }

    @Test
    public void getAll(){
        dao.deleteAll();

        List<User> user0 = dao.getAll();
        assertThat(user0.size(),is(0));

        dao.add(user1); // id :gyumee
        List<User> users1 = dao.getAll();
        assertThat(users1.size(),is(1));
        checkSameUser(user1,users1.get(0));

        dao.add(user2); // id :leegw700
        List<User> users2 = dao.getAll();
        assertThat(users2.size(),is(2));
        checkSameUser(user2,users2.get(1));

        dao.add(user3); // id :bumjin
        List<User> users3 = dao.getAll();
        assertThat(users3.size(),is(3));
        checkSameUser(user3,users3.get(0));
        checkSameUser(user1,users3.get(1));
        checkSameUser(user2,users3.get(2));
    }

    @Test(expected= DataAccessException.class)
    public void duplicateKey(){
        dao.deleteAll();
        dao.add(user1);
        dao.add(user1);
    }

    @Test
    public void sqlExceptionTranslate(){
            dao.deleteAll();
            try{
                dao.add(user1);
                dao.add(user1);
            }catch(DuplicateKeyException ex){
                SQLException sqlEx = (SQLException) ex.getCause();
                SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            }
    }


    private void checkSameUser(User user1,User user2){
        assertThat(user1.getId(),is(user2.getId()));
        assertThat(user1.getName(),is(user2.getName()));
        assertThat(user1.getPassword(),is(user2.getPassword()));
    }

}
