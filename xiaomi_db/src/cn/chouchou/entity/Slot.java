package cn.chouchou.entity;

public class Slot {
	private  String machine;
	private  String slotname;//T+slot+L/R
	private  String  productname;
	private String library;
	
	private int qty;//×ÜÊý
	private int broad_qty;//°åÊý
	
	private  String   Pickup_Count;
	private  String   Error_Count;
	
	private  String  spoilage_Rate;
	private  String  error_Rate;
	
	private  String  pickup_Error;
	private  String  recognition_Error;
	private  String  thick_Error;
	private  String   placement_Error;
	private  String   part_Drop_Error;
	private  String   transfer_unit_parts_drop_error_Count;
	public String getSlotname() {
		return slotname;
	}
	public void setSlotname(String slotname) {
		this.slotname = slotname;
	}
	public String getProductname() {
		return productname;
	}
	public void setProductname(String productname) {
		this.productname = productname;
	}
	public String getLibrary() {
		return library;
	}
	public void setLibrary(String library) {
		this.library = library;
	}
	public String getPickup_Count() {
		return Pickup_Count;
	}
	public void setPickup_Count(String pickup_Count) {
		Pickup_Count = pickup_Count;
	}
	public String getError_Count() {
		return Error_Count;
	}
	public void setError_Count(String error_Count) {
		Error_Count = error_Count;
	}
	public String getSpoilage_Rate() {
		return spoilage_Rate;
	}
	public void setSpoilage_Rate(String spoilage_Rate) {
		this.spoilage_Rate = spoilage_Rate;
	}
	public String getError_Rate() {
		return error_Rate;
	}
	public void setError_Rate(String error_Rate) {
		this.error_Rate = error_Rate;
	}
	public String getPickup_Error() {
		return pickup_Error;
	}
	public void setPickup_Error(String pickup_Error) {
		this.pickup_Error = pickup_Error;
	}
	public String getRecognition_Error() {
		return recognition_Error;
	}
	public void setRecognition_Error(String recognition_Error) {
		this.recognition_Error = recognition_Error;
	}
	public String getThick_Error() {
		return thick_Error;
	}
	public void setThick_Error(String thick_Error) {
		this.thick_Error = thick_Error;
	}
	public String getPlacement_Error() {
		return placement_Error;
	}
	public void setPlacement_Error(String placement_Error) {
		this.placement_Error = placement_Error;
	}
	public String getPart_Drop_Error() {
		return part_Drop_Error;
	}
	public void setPart_Drop_Error(String part_Drop_Error) {
		this.part_Drop_Error = part_Drop_Error;
	}
	public String getTransfer_unit_parts_drop_error_Count() {
		return transfer_unit_parts_drop_error_Count;
	}
	public void setTransfer_unit_parts_drop_error_Count(
			String transfer_unit_parts_drop_error_Count) {
		this.transfer_unit_parts_drop_error_Count = transfer_unit_parts_drop_error_Count;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public int getBroad_qty() {
		return broad_qty;
	}
	public void setBroad_qty(int broad_qty) {
		this.broad_qty = broad_qty;
	}
	public String getMachine() {
		return machine;
	}
	public void setMachine(String machine) {
		this.machine = machine;
	}
	
	
	
}
