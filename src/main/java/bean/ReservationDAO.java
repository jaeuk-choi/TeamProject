package bean;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ReservationDAO {
    private Context context = null;
    private DataSource dataSource = null;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    public ReservationDAO () {
        try {
            context = new InitialContext();
            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/acorn");
        } catch (NamingException e) {
            System.out.println("[Constructor] Message : " + e.getMessage());
            System.out.println("[Constructor] Class   : " + e.getClass().getSimpleName());
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
    
    //reservation.jsp
    public List<ReservationDTO> getReservationDTOList(String keyField, String keyWord){
    	String sql = null;
    	
    	if(keyWord == null || keyWord.isEmpty()) {
    		sql = "SELECT res_no, ser_name,  res_date, res_time, cus_name, res_comm FROM res "
            		+ "INNER JOIN cus ON cus.cus_id = res.cus_id INNER JOIN ser ON ser.ser_code = res.ser_code";
    	}
    	else {
    		sql = "SELECT res_no, ser_name,  res_date, res_time, cus_name, res_comm FROM res "
            		+ "INNER JOIN cus ON cus.cus_id = res.cus_id INNER JOIN ser ON ser.ser_code = res.ser_code "
    				+ "WHERE " + keyField + " like '%" + keyWord + "%'";
    	}
    	
    	ArrayList list = new ArrayList();
    	
    	try {
    		context = new InitialContext();
    		dataSource = (DataSource)context.lookup("java:comp/env/jdbc/acorn");
    		connection = dataSource.getConnection();

    		statement = connection.prepareStatement(sql);
    		resultSet = statement.executeQuery();
    		
    		while(resultSet.next()) {
    			ReservationDTO reservationDTO = new ReservationDTO();
    			reservationDTO.setRes_no(resultSet.getInt("res_no"));
    			reservationDTO.setSer_name(resultSet.getString("ser_name"));
    			reservationDTO.setRes_date(resultSet.getString("res_date"));
    			reservationDTO.setRes_time(resultSet.getString("res_time"));
    			reservationDTO.setCus_name(resultSet.getString("cus_name"));
    			reservationDTO.setRes_comm(resultSet.getString("res_comm"));
    			
    			list.add(reservationDTO);
    		}
    		
    	}
    	catch(Exception err) {
    		System.out.println("Error : " + err);
    	}
    	finally {
    		freeConnection();
    	}
		return list;
    }
    
    
    //reservationPostProc.jsp 
    public void setReservationDTO(ReservationDTO reservationDTO) throws SQLException, ClassNotFoundException {
        String sql = null;

        try { 
        	context = new InitialContext();
    		dataSource = (DataSource)context.lookup("java:comp/env/jdbc/acorn");
    		connection = dataSource.getConnection();
    		
    		// cus_id와 ser_code를 조회하는 쿼리
    		sql = "SELECT c.cus_id, s.ser_code FROM cus c, ser s WHERE c.cus_name = ? AND s.ser_name = ?";
    		statement = connection.prepareStatement(sql);
    		statement.setString(1, reservationDTO.getCus_name());
    		statement.setString(2, reservationDTO.getSer_name());

    		resultSet = statement.executeQuery();

    		if(resultSet.next()) {
    		    int cus_id = resultSet.getInt("cus_id");
    		    String ser_code = resultSet.getString("ser_code");

    		    // res 테이블에 삽입하는 쿼리
    		    sql = "INSERT INTO res (cus_id, ser_code, res_date, res_time, res_comm) VALUES (?, ?, ?, ?, ?)";
    		    statement = connection.prepareStatement(sql);
    		    statement.setInt(1, cus_id);
    		    statement.setString(2, ser_code);
    		    statement.setString(3, reservationDTO.getRes_date());
    		    statement.setString(4, reservationDTO.getRes_time());
    		    statement.setString(5, reservationDTO.getRes_comm());
    		    statement.executeUpdate();
    		    
    		    //ser_cnt 증가
    		    sql = "UPDATE ser SET ser_cnt = ser_cnt + 1 WHERE ser_code = ?";
    		    statement = connection.prepareStatement(sql);
    		    statement.setString(1, ser_code);
    		    statement.executeUpdate();
    		}

        } 
        catch(Exception err) {
        	System.out.println("Error : "  + err);
        }
        finally {
            freeConnection();
        }
    }
    
    //reservationPost.jsp 
    //예약자명 조회
    public List<String> getAllCustomerNames() throws SQLException{
		List<String> customerNames = new ArrayList<>();
		String query = "SELECT cus_id, cus_name FROM cus WHERE cus_name=?"; //'cus' 테이블에서 회원명 가져옴
    	
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(query);
			
			//statement.setString(1, "cus_name");
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) {
				customerNames.add(resultSet.getString("cus_name"));
			}
		} finally {
			freeConnection();
		}
		return customerNames;
    }
    
    //예약 서비스명 조회
    public List<String> getAllServiceNames() throws SQLException {
	    List<String> serviceNames = new ArrayList<>();
	    String query = "SELECT ser_name FROM ser"; // 'ser' 테이블에서 서비스 명 가져옴

	    try {
	        connection = dataSource.getConnection();
	        statement = connection.prepareStatement(query);
	        resultSet = statement.executeQuery();

	        while (resultSet.next()) {
	            serviceNames.add(resultSet.getString("ser_name"));
	        }
	    } finally {
	        freeConnection(); // freeConnection을 finally 블록에서 호출하여 자원을 반환
	    }
	    return serviceNames;
	}
    
    
    //reservationRead.jsp , reservationUpdate.jsp
    public ReservationDTO getReservationDTO(int res_no) {
    	String sql = null;
    	ReservationDTO dto = new ReservationDTO();
    	
    	try {
    		context = new InitialContext();
            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/acorn");
            connection = dataSource.getConnection();
            
            sql = "SELECT * FROM res "
            		+ "INNER JOIN cus ON cus.cus_id = res.cus_id INNER JOIN ser ON ser.ser_code = res.ser_code "
            		+ "WHERE res_no=?";
            statement = connection.prepareStatement(sql);
            
            statement.setInt(1, res_no);
            resultSet = statement.executeQuery();
            
            if(resultSet.next()) {
            	dto.setRes_no(resultSet.getInt("res_no"));
            	dto.setCus_id(resultSet.getInt("cus_id"));
            	dto.setCus_name(resultSet.getString("cus_name"));
            	dto.setRes_comm(resultSet.getString("res_comm"));
            	dto.setRes_date(resultSet.getString("res_date"));     	
            	dto.setRes_time(resultSet.getString("res_time"));
            	dto.setSer_code(resultSet.getString("ser_code"));
            	dto.setSer_name(resultSet.getString("ser_name"));
            }
    	}
    	catch(Exception err) {
    		System.out.println("Error : " + err);
    	}
    	finally {
    		freeConnection();
    	}
    	return dto;
    }
    
    
    //reservationUpdateProc.jsp 
    public void updateReservationDTO(ReservationDTO resDto) {
        String sql = null;

        try {
            context = new InitialContext();
            dataSource = (DataSource)context.lookup("java:comp/env/jdbc/acorn");
            connection = dataSource.getConnection();
            
            // res 테이블에서 기존 ser_code 조회
            sql = "SELECT ser_code FROM res WHERE res_no = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, resDto.getRes_no()); 
            resultSet = statement.executeQuery();
            
            String old_ser_code = null;
            if (resultSet.next()) {
                old_ser_code = resultSet.getString("ser_code");
            }
            
            // cus_id와 ser_code를 조회하는 쿼리
            sql = "SELECT c.cus_id, s.ser_code FROM cus c, ser s WHERE c.cus_name = ? AND s.ser_name = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, resDto.getCus_name());
            statement.setString(2, resDto.getSer_name());
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int cus_id = resultSet.getInt("cus_id");
                String new_ser_code = resultSet.getString("ser_code");

                // ser_code가 변경된 경우에만 ser_cnt 수정
                if (!old_ser_code.equals(new_ser_code)) {
                    // 기존 ser_code의 ser_cnt 감소
                    sql = "UPDATE ser SET ser_cnt = ser_cnt - 1 WHERE ser_code = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, old_ser_code);
                    statement.executeUpdate();

                    // 새로운 ser_code의 ser_cnt 증가
                    sql = "UPDATE ser SET ser_cnt = ser_cnt + 1 WHERE ser_code = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, new_ser_code);
                    statement.executeUpdate();
                }

                // res 테이블 수정
                sql = "UPDATE res SET cus_id = ?, ser_code = ?, res_date = ?, res_time = ?, res_comm = ? WHERE res_no = ?";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, cus_id);
                statement.setString(2, new_ser_code);
                statement.setString(3, resDto.getRes_date());
                statement.setString(4, resDto.getRes_time());
                statement.setString(5, resDto.getRes_comm());
                statement.setInt(6, resDto.getRes_no());
                statement.executeUpdate();
            }
        } catch (Exception err) {
            System.out.println("Error : " + err);
        } finally {
            freeConnection();
        }
    }

    
    //reservationDelete.jsp
	public void deleteReservationDTO(int res_no) throws SQLException {

		String sql;

		try {
			context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/acorn");
			connection = dataSource.getConnection();

			// res 테이블에서 ser_code 조회
			sql = "SELECT ser_code FROM res WHERE res_no=?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, res_no);
			resultSet = statement.executeQuery();

			// resultSet을 통해 ser_code를 가져옴
			String ser_code = null;
			if (resultSet.next()) {
				ser_code = resultSet.getString("ser_code");
			}

			// 예약 삭제하는 쿼리
			sql = "DELETE FROM res WHERE res_no=?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, res_no);
			statement.executeUpdate();

			// ser_cnt 감소하는 쿼리
			sql = "UPDATE ser SET ser_cnt = ser_cnt - 1 WHERE ser_code = ?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, ser_code);
			statement.executeUpdate();

		} catch (Exception err) {
			System.out.println("Error : " + err);
		} finally {
			freeConnection();
		}
	  }
	
	
	
    }
