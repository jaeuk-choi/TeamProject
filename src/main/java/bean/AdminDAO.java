package bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class AdminDAO {
    private Context context = null;
    private DataSource dataSource = null;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    
    private String ADMIN_LIST = "select * from admin";
    private String ADMIN_INSERT = "insert into admin values(?, ?, ?, ?, ?)";
    private	String ADMIN_GET = "select * from admin where ad_id = ?";
    
    public AdminDAO () {
        try {
            context = new InitialContext();
            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/acorn");
        } catch (NamingException e) {
            System.out.println("[AdminDAO] Message : " + e.getMessage());
            System.out.println("[AdminDAO] Class   : " + e.getClass().getSimpleName());
        }
    }

    /* DB 연결 해제 */
    public void freeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            System.out.println("[freeConnection] Message : " + e.getMessage());
            System.out.println("[freeConnection] Class   : " + e.getClass().getSimpleName());
        }
    }
    
    // 회원 등록
    public void insertAdmin(AdminDTO dto) {
    	try {
    		Class.forName("org.mariadb.jdbc.Driver");
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(ADMIN_INSERT);
            statement.setString(1, dto.getAd_id());
            statement.setString(2, dto.getAd_pw());
            statement.setString(3, dto.getAd_name());
            statement.setString(4, dto.getAd_ph());
            statement.setString(5, dto.getAd_mail());
            statement.executeUpdate();
    		
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("[getAdmin] Message : " + e.getMessage());
            System.out.println("[getAdmin] Class   : " + e.getClass().getSimpleName());
        } finally {
            freeConnection();
        }
    }
    
    // 로그인 회원 조회
    public AdminDTO getAdmin(AdminDTO dto) {
    	AdminDTO admin = null;
    	try {
    		Class.forName("org.mariadb.jdbc.Driver");
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(ADMIN_GET);
    		statement.setString(1, dto.getAd_id());
    		resultSet = statement.executeQuery();
    		
    		while(resultSet.next()) {
                admin = new AdminDTO();
                admin.setAd_id(resultSet.getString("ad_id"));
                admin.setAd_pw(resultSet.getString("ad_pw"));
                admin.setAd_name(resultSet.getString("ad_name"));
                admin.setAd_ph(resultSet.getString("ad_ph"));
                admin.setAd_mail(resultSet.getString("ad_mail"));
    		}
    	} catch (Exception e) {
            System.out.println("[getAdmin] Message : " + e.getMessage());
            System.out.println("[getAdmin] Class   : " + e.getClass().getSimpleName());
        } finally {
            freeConnection();
        }
		return admin;
    }
  
/*  
    // 저장할 자료구조로 변경해주세요.
    public java.util.Set<AdminDTO> getAdmin(String ad_id) {
        java.util.Set<AdminDTO> set = new java.util.HashSet<>();
        String sql;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = dataSource.getConnection();

            sql = "SELECT * FROM admin WHERE ad_id=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, ad_id);
            resultSet = statement.executeQuery();

            while(resultSet.next()) {
                AdminDTO DTO = new AdminDTO();
                DTO.setAd_id(resultSet.getString("ad_id"));
                DTO.setAd_name(resultSet.getString("ad_name"));
                DTO.setAd_ph(resultSet.getString("ad_ph"));
                DTO.setAd_mail(resultSet.getString("ad_mail"));

                set.add(DTO);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("[getAdmin] Message : " + e.getMessage());
            System.out.println("[getAdmin] Class   : " + e.getClass().getSimpleName());
        } finally {
            freeConnection();
        }
        return set;
    }
*/
}