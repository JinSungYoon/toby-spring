package springbook.user;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class UserDaoTest {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {

//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        UserDao dao = context.getBean("userDao",UserDao.class);

        UserDao dao1 = context.getBean("userDao",UserDao.class);

        System.out.println(dao.toString());
        System.out.println(dao1.toString());

        dao.deleteAll();

        User user = new User();
        user.setId("whiteship");
        user.setName("��⼱");
        user.setPassword("married");

        dao.add(user);
        System.out.println(user.getId()+" ��� ����");
        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println(user2.getId()+" ��ȸ ����");

    }

}