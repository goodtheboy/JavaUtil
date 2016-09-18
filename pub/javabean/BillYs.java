package pub.javabean;

import java.math.BigDecimal;

import pub.javabean.base.SuperVO;

public class BillYs extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String createtime;
	private String billtype;
	private String tzdbh;
	private String memo;
	private String direction;
	private String state;
	private String createperson;
	private Integer id;
	private BigDecimal tzje;
	private String custname;
	private String custid;
	private String audittime;
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getBilltype() {
		return billtype;
	}
	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}
	public String getTzdbh() {
		return tzdbh;
	}
	public void setTzdbh(String tzdbh) {
		this.tzdbh = tzdbh;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCreateperson() {
		return createperson;
	}
	public void setCreateperson(String createperson) {
		this.createperson = createperson;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigDecimal getTzje() {
		return tzje;
	}
	public void setTzje(BigDecimal tzje) {
		this.tzje = tzje;
	}
	public String getCustname() {
		return custname;
	}
	public void setCustname(String custname) {
		this.custname = custname;
	}
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	public String getAudittime() {
		return audittime;
	}
	public void setAudittime(String audittime) {
		this.audittime = audittime;
	}
	@Override
	public String getPKFieldName() {
		return "Id";
	}
	@Override
	public String getTableName() {
		return "bill_ys";
	}
	
}
