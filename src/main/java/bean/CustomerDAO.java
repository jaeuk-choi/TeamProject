package bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class CustomerDAO {

    private Context context = null;
    private DataSource dataSource = null;

    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;

    public CustomerDAO () {
        try {
            context = new InitialContext();
            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/acorn");
        } catch (NamingException e) {
            System.out.println("[CustomerDAO] Message : " + e.getMessage());
            System.out.println("[CustomerDAO] Class   : " + e.getClass().getSimpleName());
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
  
 // customer.jsp
 	public List<CustomerDTO> getCustomer (String keyField, String keyWord) {
 		String sql = null;
 		
 		if(keyWord == null || keyWord.isEmpty()) {
 			sql = "SELECT * FROM cus";
 		}
 		else {
 			sql = "SELECT * FROM cus WHERE " + keyField + " LIKE '%" + keyWord + "%'";
 		}
 		
 		//전달할 쿼리 준비(어떤 명령어를 db에 전달할지)
 		
 		ArrayList list = new ArrayList();
 		
 		try {
 			connection = dataSource.getConnection();
 			statement = connection.prepareStatement(sql);
 			resultSet = statement.executeQuery();
 			
 		
 			while(resultSet.next()) {
 				CustomerDTO customer = new CustomerDTO();
 				customer.setCus_id(resultSet.getInt("cus_id"));
 				customer.setCus_name(resultSet.getString("cus_name"));
 				customer.setCus_gender(resultSet.getString("cus_gender"));
 				customer.setCus_ph(resultSet.getString("cus_ph"));
 				customer.setCus_mail(resultSet.getString("cus_mail"));
 				customer.setCus_reg(resultSet.getString("cus_reg"));
 				customer.setCus_rank(resultSet.getString("cus_rank"));
 				customer.setCus_note(resultSet.getString("cus_note"));
 				
 				list.add(customer);
 			}
 		}
 		catch(Exception err) {
 			System.out.println("getCustomer : " + err);
 		}
 		finally {
 			freeConnection();
 		}
 		return list;
 	}
    
 // 새로운 메소드 추가 (customerDTOList 검색)
    public List<CustomerDTO> getCustomerDTOList(String keyField, String keyWord) {
        String sql = null;

        if (keyWord == null || keyWord.isEmpty()) {
            sql = "SELECT * FROM cus";
        } else {
            sql = "SELECT * FROM cus WHERE " + keyField + " LIKE ?";
        }

        ArrayList<CustomerDTO> customerList = new ArrayList<>();

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);

            if (keyWord != null && !keyWord.isEmpty()) {
                statement.setString(1, "%" + keyWord + "%");
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                CustomerDTO customer = new CustomerDTO();
                customer.setCus_id(resultSet.getInt("cus_id"));
                customer.setCus_name(resultSet.getString("cus_name"));
                customer.setCus_gender(resultSet.getString("cus_gender"));
                customer.setCus_ph(resultSet.getString("cus_ph"));
                customer.setCus_mail(resultSet.getString("cus_mail"));
                customer.setCus_reg(resultSet.getString("cus_reg"));
                customer.setCus_rank(resultSet.getString("cus_rank"));
                customer.setCus_note(resultSet.getString("cus_note"));

                customerList.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("[getCustomerDTOList] Message : " + e.getMessage());
            System.out.println("[getCustomerDTOList] Class   : " + e.getClass().getSimpleName());
        } finally {
            freeConnection();
        }
        return customerList;
    }
    
// 등록 기간별 회원 조회
    public List<CustomerDTO> getCustomerByregdate(String startDate, String endDate) {
    	String sql = "SELECT * FROM cus WHERE cus_reg BETWEEN ? AND ?";
    	ArrayList<CustomerDTO> customerList = new ArrayList<>();
        
        try {
        	connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, startDate);
            statement.setString(2, endDate);


            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                CustomerDTO customer = new CustomerDTO();
                customer.setCus_id(resultSet.getInt("cus_id"));
                customer.setCus_name(resultSet.getString("cus_name"));
                customer.setCus_gender(resultSet.getString("cus_gender"));
                customer.setCus_ph(resultSet.getString("cus_ph"));
                customer.setCus_mail(resultSet.getString("cus_mail"));
                customer.setCus_reg(resultSet.getString("cus_reg"));
                customer.setCus_rank(resultSet.getString("cus_rank"));
                customer.setCus_note(resultSet.getString("cus_note"));

                customerList.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("[getCustomerByregdate] Message : " + e.getMessage());
            System.out.println("[getCustomerByregdate] Class   : " + e.getClass().getSimpleName());
        } finally {
            freeConnection();
        }
        return customerList;
    }
    
    
    //reservationPost.jsp 
 // 예약자명 조회
    public List<String> getAllCustomerNames() throws SQLException {
        List<String> customerNames = new ArrayList<>();
        String sql = "SELECT cus_name FROM cus";

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                customerNames.add(resultSet.getString("cus_name"));
            }
        } catch (SQLException e) {
            System.out.println("[getAllCustomerNames] Message : " + e.getMessage());
            System.out.println("[getAllCustomerNames] Class   : " + e.getClass().getSimpleName());
        } finally {
            freeConnection();
        }
        return customerNames;
    }
}