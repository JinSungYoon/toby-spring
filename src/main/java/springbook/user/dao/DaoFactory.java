package springbook.user.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DaoFactory {
	
	private ConnectionMaker connectionMaker;

    @Bean
    public UserDao userDao(){
        UserDao  userDao = new UserDao();
        userDao.setDataSource(dataSource());
        return userDao;
    }
    
    @Bean
    public DataSource dataSource() {
    	SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
    	
    	dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
    	dataSource.setUrl("jdbc:mysql://localhost/springbook");
    	dataSource.setUsername("root");
    	dataSource.setPassword("0000");
    	
    	return dataSource;
    }

    @Bean
    public ConnectionMaker connectionMaker(){
        return new SimpleConnectionMaker();
    }

}