package cn.chouchou.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;

import cn.chouchou.entity.LineInfo;
import cn.chouchou.entity.Machine;
import cn.chouchou.entity.Slot;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JDBCUtil {
	private static ComboPooledDataSource  datasorce;
	static{
		datasorce=new ComboPooledDataSource();
	}
	public static QueryRunner  getQueryRunner(){
		return  new QueryRunner(datasorce);
	}
	
	
	/**
	 * 写入10.2.0.25 数据库 传入 查询参数
	 * CREATE  TABLE  Xiaomi_Line(
	num  INT IDENTITY(1,1) PRIMARY KEY,
	spoilage_Rate  INT ,
    total_Pickup_Count  INT ,
	total_Error_Count  INT ,
	pickup_Error  INT ,
	recognition_Error  INT ,
	thick_Error  INT ,
	placement_Error  INT ,
	part_Drop_Error  INT ,
	transfer_unit_parts_drop_error_Count  INT ,
	selectPara NVARCHAR(200),
	createdate  DATETIME  DEFAULT(GETDATE())
	)
	 * */
	/**
	 * 最初线体信息
	 * */
	public static void  insert(QueryRunner  qr,LineInfo  line,String info)throws Exception{
		  String sql="insert into  Xiaomi_Line (spoilage_Rate, total_Pickup_Count,total_Error_Count,pickup_Error,recognition_Error, " +
				  " thick_Error,placement_Error,part_Drop_Error,transfer_unit_parts_drop_error_Count,selectPara) " +
				  "values(?,?,?,?,?,?,?,?,?,? )";
		
		  qr.update(sql,new Object[]{
			  line.getSpoilage_Rate(),
			  line.getTotal_Pickup_Count(),
			  line.getTotal_Error_Count(),
			  line.getPickup_Error(),
			  line.getRecognition_Error(),
			  line.getThick_Error(),
			  line.getPlacement_Error(),
			  line.getPart_Drop_Error(),
			  line.getTransfer_unit_parts_drop_error_Count(),
			  info.toString(),
		  });
    
		
	}
	
	
	/**
	 * 详细每轨槽位信息
	 * */
	public static void  insertSlots(QueryRunner  qr,Map<String,Object> res,String info)throws Exception{
		  String sql="INSERT INTO dbo.xiaomi_slot ( slot ,qty , broad_qty, productname ,library , Pickup_Count ,Error_Count ,spoilage_Rate ,error_Rate ,pickup_Error ,"+
         " recognition_Error ,thick_Error , placement_Error ,part_Drop_Error ,transfer_unit_parts_drop_error_Count ,selectPara " +
        " ) VALUES  ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		   if(res==null||res.size()==0){
			   return ;
		   }
		   List<Slot> slots=(List<Slot>) res.get("slots");
		   
		   if(slots!=null){
			   int slot_num=slots.size();
			   String[][] params=new String[slot_num][];
			   //开始注入批处理参数;
			   Slot slot=null;
			   for(int x=0;x<slot_num;x++){
				   slot=slots.get(x);
				   params[x]=new String[]{
						   slot.getMachine()+";"+slot.getSlotname(),
						   res.get("qty").toString(),
						   res.get("main_qty").toString(),
						   slot.getProductname(),
						   slot.getLibrary(),
						   slot.getPickup_Count(),
						   slot.getError_Count(),
						   slot.getSpoilage_Rate(),
						   slot.getError_Rate(),
						   slot.getPickup_Error(),
						   slot.getRecognition_Error(),
						   slot.getThick_Error(),
						   slot.getPlacement_Error(),
						   slot.getPart_Drop_Error(),
						   slot.getTransfer_unit_parts_drop_error_Count(),
						   info
						   };
			   }
			   qr.batch(sql, params);
			   System.out.println("params1=============================="+params);
		   }
		  
		 
  
		
	}

	/**
	 * 机台停机信息插入
	 * */
	public static void insertMachines(QueryRunner queryRunner,
			List<Machine> machines, String info_db) throws Exception{
		// TODO Auto-generated method stub
		if(machines==null)
			return ;
		int size=machines.size();
		if(size==0)
			return ;
		Object[][] paras=new Object[size][];
		for(int x=0;x<size;x++){
			Machine m=machines.get(x);
			paras[x]=new Object[]{
					m.getMachine(),m.getStage(),m.getRunning_Rate(),m.getReal_Running_Time(),
					m.getTotal_Stop_Time(),m.getQty(),m.getBroad_qty(),m.getSpoilage_Rate(),
					m.getTotal_Pickup_Count(),m.getTotal_Error_Count(),m.getPickup_Error(),m.getRecognition_Error(),
					m.getThick_Error(),m.getPlacement_Error(),m.getPart_Drop_Error(),m.getTransfer_unit_parts_drop_error_Count(),
					info_db
			};
		}
		String sql="INSERT INTO  dbo.Xiaomi_Machine ( Machine ,Stage , Running_Rate , Real_Running_Time ,Total_Stop_Time ,Qty ,Broad_Qty ,Spoilage_Rate ," +
				" Total_Pickup_Count ,Total_Error_Count , Pickup_Error ,Recognition_Error ,Thick_Error ,Placement_Error ,Part_Drop_Error ," +
				" Transfer_Unit_Parts_Drop_Error_Count ,SelectPara) VALUES  ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?  )";
		
		queryRunner.batch(sql, paras);
		System.out.println("params2================================"+paras);
	}
}
