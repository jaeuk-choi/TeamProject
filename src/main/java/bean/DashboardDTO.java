package bean;

public class DashboardDTO {
    private String pd_name;
    private int pd_ea;
    private String res_time;
	private String ser_name;
	private String ser_code;
    private int ser_price; 
    private int ser_cnt;
    
    public String getPd_name() {
		return pd_name;
	}
	public void setPd_name(String pd_name) {
		this.pd_name = pd_name;
	}
	public int getPd_ea() {
		return pd_ea;
	}
	public void setPd_ea(int pd_ea) {
		this.pd_ea = pd_ea;
	}
	public String getRes_time() {
		return res_time;
	}
	public void setRes_time(String res_time) {
		this.res_time = res_time;
	}
	public String getSer_name() {
		return ser_name;
	}
	public void setSer_name(String ser_name) {
		this.ser_name = ser_name;
	}
	public String getSer_code() {
		return ser_code;
	}
	public void setSer_code(String ser_code) {
		this.ser_code = ser_code;
	}
	public int getSer_price() {
		return ser_price;
	}
	public void setSer_price(int ser_price) {
		this.ser_price = ser_price;
	}
	public int getSer_cnt() {
		return ser_cnt;
	}
	public void setSer_cnt(int ser_cnt) {
		this.ser_cnt = ser_cnt;
	}
	/* 월별 서비스 매출 */
	public int getChart_revenue() {
		return ser_price * ser_cnt;
	}
}
