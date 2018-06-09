package com.zowee.mes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.PreparedOperateMoNamesAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 物料备料的业务逻辑处理类 
 */
public class HWTmisGetCartonWeightModel extends CommonModel 
{


	/**
	 * @param task
	 * @description 该任务用于对华为TMIS系统中中箱重量检查
	 */
	
	public void GetCartonWeight(Task task)
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String palletNo = (String)task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.setWsMethod("GetWight");
					soap.setWsWsdl("http://10.2.0.7/TmisWebService/Service1.asmx");
					soap.addWsProperty("PalletNo", palletNo);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					Log.i(TAG, ""+envolop.bodyIn);
					String getdata = envolop.bodyIn.toString();
					task.setTaskResult(MesWebService.GetHwTmisreturndata(getdata));

				}

				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);	
	}

}


