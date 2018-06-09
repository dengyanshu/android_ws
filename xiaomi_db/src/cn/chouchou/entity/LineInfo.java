package cn.chouchou.entity;

public class LineInfo {
	
	
	 
	
	
	private  int  spoilage_Rate;
	private  int   total_Pickup_Count;
	private  int  total_Error_Count;
	private  int  pickup_Error;
	private  int  recognition_Error;
	private  int  thick_Error;
	private  int   placement_Error;
	private  int   part_Drop_Error;
	private  int   transfer_unit_parts_drop_error_Count;
	public int getSpoilage_Rate() {
		return spoilage_Rate;
	}
	public void setSpoilage_Rate(int spoilage_Rate) {
		this.spoilage_Rate = spoilage_Rate;
	}
	public int getTotal_Pickup_Count() {
		return total_Pickup_Count;
	}
	public void setTotal_Pickup_Count(int total_Pickup_Count) {
		this.total_Pickup_Count = total_Pickup_Count;
	}
	public int getTotal_Error_Count() {
		return total_Error_Count;
	}
	public void setTotal_Error_Count(int total_Error_Count) {
		this.total_Error_Count = total_Error_Count;
	}
	public int getPickup_Error() {
		return pickup_Error;
	}
	public void setPickup_Error(int pickup_Error) {
		this.pickup_Error = pickup_Error;
	}
	public int getRecognition_Error() {
		return recognition_Error;
	}
	public void setRecognition_Error(int recognition_Error) {
		this.recognition_Error = recognition_Error;
	}
	public int getThick_Error() {
		return thick_Error;
	}
	public void setThick_Error(int thick_Error) {
		this.thick_Error = thick_Error;
	}
	public int getPlacement_Error() {
		return placement_Error;
	}
	public void setPlacement_Error(int placement_Error) {
		this.placement_Error = placement_Error;
	}
	public int getPart_Drop_Error() {
		return part_Drop_Error;
	}
	public void setPart_Drop_Error(int part_Drop_Error) {
		this.part_Drop_Error = part_Drop_Error;
	}
	public int getTransfer_unit_parts_drop_error_Count() {
		return transfer_unit_parts_drop_error_Count;
	}
	public void setTransfer_unit_parts_drop_error_Count(
			int transfer_unit_parts_drop_error_Count) {
		this.transfer_unit_parts_drop_error_Count = transfer_unit_parts_drop_error_Count;
	}
	@Override
	public String toString() {
		return "LineInfo [spoilage_Rate=" + spoilage_Rate
				+ ", total_Pickup_Count=" + total_Pickup_Count
				+ ", total_Error_Count=" + total_Error_Count
				+ ", pickup_Error=" + pickup_Error + ", recognition_Error="
				+ recognition_Error + ", thick_Error=" + thick_Error
				+ ", placement_Error=" + placement_Error + ", part_Drop_Error="
				+ part_Drop_Error + ", transfer_unit_parts_drop_error_Count="
				+ transfer_unit_parts_drop_error_Count + "]";
	}
	
	
	
	

}
