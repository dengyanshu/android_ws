package cn.chouchou.entity;

public class Machine {
    
	   
	/*
	 * 
	 * 停机时间封装
	 * 
	 * */
	

     private  String machine;
     private  String stage;
	
	private int qty;//总数
	private int broad_qty;//板数
	
	private  String running_Rate;
	private  String real_Running_Time;
	private String total_Stop_Time;
	
	private  String  spoilage_Rate;
	//private  String  error_Rate;
	
	private  String   Total_Pickup_Count;
	private  String   Total_Error_Count;
	
	
	private  String  pickup_Error;
	private  String  recognition_Error;
	private  String  thick_Error;
	private  String   placement_Error;
	private  String   part_Drop_Error;
	private  String   transfer_unit_parts_drop_error_Count;
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
	public String getRunning_Rate() {
		return running_Rate;
	}
	public void setRunning_Rate(String running_Rate) {
		this.running_Rate = running_Rate;
	}
	public String getReal_Running_Time() {
		return real_Running_Time;
	}
	public void setReal_Running_Time(String real_Running_Time) {
		this.real_Running_Time = real_Running_Time;
	}
	public String getTotal_Stop_Time() {
		return total_Stop_Time;
	}
	public void setTotal_Stop_Time(String total_Stop_Time) {
		this.total_Stop_Time = total_Stop_Time;
	}
	public String getSpoilage_Rate() {
		return spoilage_Rate;
	}
	public void setSpoilage_Rate(String spoilage_Rate) {
		this.spoilage_Rate = spoilage_Rate;
	}

	public String getTotal_Pickup_Count() {
		return Total_Pickup_Count;
	}
	public void setTotal_Pickup_Count(String total_Pickup_Count) {
		Total_Pickup_Count = total_Pickup_Count;
	}
	public String getTotal_Error_Count() {
		return Total_Error_Count;
	}
	public void setTotal_Error_Count(String total_Error_Count) {
		Total_Error_Count = total_Error_Count;
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
	public String getMachine() {
		return machine;
	}
	public void setMachine(String machine) {
		this.machine = machine;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	
	
	
}
