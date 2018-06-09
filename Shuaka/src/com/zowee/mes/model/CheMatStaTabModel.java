package com.zowee.mes.model;

import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 查料站表业务逻辑类
 */
public class CheMatStaTabModel extends CommonModel {

	private static String SMTMountName = "SMTMountName";
	private static String SMTLineNO = "SMTLineNO";
	private static String StationNo = "StationNo";
	private static String SLotNO = "SLotNO";
	private static String ProductName = "ProductName";
	private static String ProductDescription = "ProductDescription";
	private static String BaseQty = "BaseQty";
	private static String SwitchLine = "\n";

	// private static final String COLUMN_1 = "Column1";

	/**
	 * @param task
	 * @description 查料站表
	 */
	public void cheMatStaTab(Task task) {
		// if(null==task||null==task.getTaskData())
		// return;
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					String docStaNo = (String) task.getTaskData();
					String docNo = docStaNo.substring(0, 3);
					String staNo = docStaNo.substring(3, 4);
					// String sLotNo = "";
					// if(staLot.length()>=14)
					// sLotNo = staLot.substring(4,14);
					// else
					// sLotNo = staLot.substring(4);
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty(
							"SQLString",
							"declare @docNo nvarchar(50) SELECT @docNo = isnull(dbo.Workcenter.WorkcenterName,'') FROM dbo.Resource INNER JOIN dbo.Workcenter ON dbo.Resource.WorkcenterId = dbo.Workcenter.WorkcenterId WHERE dbo.Resource.ResourceName = '"
									+ docNo
									+ staNo
									+ "'; select s.SMTMountName, s.SMTLineNO, s.StationNo, si.SLotNO,pr.ProductName,p.ProductDescription,si.BaseQty, SUM(ls.BeginQty) AS MountQTY, SUM(l.Qty) AS ResidueQTY from lot l inner join LotOnSMT ls on l.LotId = ls.LotId inner join SMTMountItem si on ls.SMTMountItemId = si.SMTMountItemId inner join SMTMount s on si.SMTMountId = s.SMTMountId "
									+ " inner join Product p on si.ProductId = p.ProductId inner join ProductRoot pr on p.ProductRootId = pr.ProductRootId where ls.SMTLineNO = @docNo and ls.StationNO = (@docNo + '"
									+ staNo
									+ "')  "
									+ "  GROUP BY s.SMTMountName, s.SMTLineNO,s.StationNo, si.SLotNO, pr.ProductName,p.ProductDescription, si.BaseQty ORDER BY s.StationNo, si.SLotNO");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					String str = replaceResStr(envolop.bodyIn.toString());
					Log.i(TAG, str);
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(str);
					if (null == resDataSet || 0 == resDataSet.size())
						return task;
					// Log.i(TAG, "COME IN3");
					String resCount = String.valueOf(resDataSet.size());
					StringBuffer sbf = new StringBuffer();
					for (int i = 0; i < resDataSet.size(); i++)
						sbf.append(resDataSet.get(i) + "\n\n");
					sbf.append("\n\n\n");
					// Log.i(TAG, "COME IN4");
					String[] reses = new String[] { resCount, sbf.toString() };
					task.setTaskResult(reses);
				} catch (Exception e) {
					MesUtils.netConnFail(task.getActivity());
					Log.i(TAG, e.toString());
				}

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

	private String replaceResStr(String str) {
		if (StringUtils.isEmpty(str))
			return null;
		str = str.replaceAll(SwitchLine, "");
		str = str.replaceAll(SMTMountName, "上料名");
		str = str.replaceAll(SMTMountName, "上料名");
		str = str.replaceAll(SLotNO, "站位");
		str = str.replaceAll(SMTLineNO, "产线");
		str = str.replaceAll(StationNo, "机台");
		str = str.replaceAll(ProductName, "产品名称");
		str = str.replaceAll(ProductDescription, "产品描述");
		str = str.replaceAll(BaseQty, "上料数量");

		return str;
	}

	// public CharSequence highLightWbText(CharSequence str,String regex,int
	// color)
	// {
	// List<int[]> indItemArr = StringUtils.getMatStrIndex(regex,
	// str.toString());
	// SpannableString spanStr = new SpannableString(str);
	// for(int[] indItem : indItemArr)
	// {
	// ForegroundColorSpan spanCondition = new ForegroundColorSpan(color);
	// spanStr.setSpan(spanCondition, indItem[0], indItem[1],
	// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	// }
	//
	// return spanStr;
	// }

	public CharSequence hignRetMsg(CharSequence res1, CharSequence res2,
			int color) {
		// CharSequence strTemp = highLightWbText(str, regex, color);
		// strTemp = highLightWbText(str, "站位", color);
		// strTemp = highLightWbText(str, "产线", color);
		// strTemp = highLightWbText(str, "机台", color);
		// strTemp = highLightWbText(str, "产品名称", color);
		// strTemp = highLightWbText(str, "产品描述", color);
		// strTemp = highLightWbText(str, "上料数量", color);
		SpannableString spanStr = new SpannableString(res1.toString()
				+ res2.toString());
		ForegroundColorSpan spanCondition = new ForegroundColorSpan(color);
		spanStr.setSpan(spanCondition, 0, res1.toString().length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spanStr;
	}

	public void getSlotCount(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					String docStaNo = (String) task.getTaskData();
					String docNo = docStaNo.substring(0, 3);
					String staNo = docStaNo.substring(3, 4);
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty(
							"SQLString",
							"select count(b.SLotNO) from (select distinct sm.SLotNO from SMTMount s inner join SMTMountItem sm on s.SMTMountId = sm.SMTMountId where s.SMTLineNO = '"
									+ docNo
									+ "' and s.StationNo = '"
									+ staNo
									+ "') b;");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					// String str = replaceResStr(envolop.bodyIn.toString());
					// Log.i(TAG, str);
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet || 0 == resDataSet.size())
						return task;
					List<HashMap<String, String>> lisMaps = MesWebService
							.getResMapsLis(resDataSet);
					if (null != lisMaps && 0 != lisMaps.size()) {
						String slotCount = lisMaps.get(0).get(COLUMN_1);
						task.setTaskResult(slotCount);
					}

				} catch (Exception e) {
					MesUtils.netConnFail(task.getActivity());
					Log.i(TAG, e.toString());
				}

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

}
