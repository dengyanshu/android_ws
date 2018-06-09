package com.zowee.mes.test;

import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.test.AndroidTestCase;
import android.util.Log;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.model.MESUpdateModel;
import com.zowee.mes.update.MESUpdate;
import com.zowee.mes.utils.HexStrConver;
import com.zowee.mes.ws.MesSoapParser;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description WebService测试类
 */
public class WebServiceTest extends AndroidTestCase 
{

	private static final String TAG = "WebServiceTest";
	
	public void testGetWsRepRes()throws Exception
	{
//		Map<String,Object> wsParas = new HashMap<String,Object>();
//		wsParas.put(WebService.WSNAMESPACE, "http://WebXml.com.cn/");
//		wsParas.put(WebService.WSMETHOD, "getMobileCodeInfo");
//		wsParas.put(WebService.WSDL, "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx");
//		wsParas.put(WebService.WSSOAPEDITION, ""+SoapSerializationEnvelope.VER12);
//		wsParas.put(WebService.WSISDOTNET,true);
//		wsParas.put("mobileCode", "15985820387");
//		wsParas.put("userId","");
		
//		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("wsconfig.properties");
//		String encoding = "utf-8";
//		Properties pro = new Properties();
//		pro.load(inStream);
		Soap soap = MesSoapParser.getOfficalSoap();
		String lotSn = "MZ01NBZY126T002L";
	//	soap.addWsProperty("SQLString", );
//		if(null!=soap)
//		{
//			Log.i(TAG, ""+soap.getWsNameSpa());
//			Log.i(TAG, ""+soap.getWsMethod());
//			Log.i(TAG, ""+soap.getWsWsdl());
//			Log.i(TAG, ""+soap.getSoapEdi());
//			Log.i(TAG, ""+soap.isDotNet());
//			
//		}
//		Soap soap = new Soap("http://tempuri.org/","GetDataSetWithSQLString","http://mes.zowee.com.cn:8008/MobileService.asmx",SoapSerializationEnvelope.VER11);
//		soap.setDotNet(true);
//		
		//soap.addWsProperty("SQLString", "Create proc fifoScan @lotSn nvarchar(30) = '' As Begin if SUBSTRING(@lotSn,1,1) <>'M' begin "+
////	"select '条码有误,请检查!' as BoxSN,'' LotSN,'' 库位 return -1 end SELECT TOP (100) percent dbo.POItemLot.BoxSN AS BoxSN, dbo.StockLoc.LotSN, dbo.stockLOC.StockLocation AS 库位  "
////    +" FROM dbo.Lot inner join dbo.stockLOC on dbo.lot.LotSN=dbo.StockLoc.LotSN	 inner join dbo.POItemLot  on  dbo.stockloc.LotSN=dbo.POItemLot.LotSN  where dbo.lot.LotSN < @lotSn ORDER BY dbo.Lot.LotSN end");  
////       "dbo.lot.LotSN=dbo.StockLoc.LotSN inner join dbo.POItemLot on dbo.stockloc.LotSN=dbo.POItemLot.LotSN  inner join dbo.matercollectlot on "+ 
////		"dbo.poitemlot.LotSN=dbo.MatercollectLot.LotSN	WHERE (dbo.Lot.LotSN <= 'M00NBZYZ112270003') AND dbo.lot.ProductId ='PRD100000E2H' ORDER BY dbo.MatercollectLot.Createdate" );
////		soap.addWsProperty("SQLString","SELECT   TOP (10)  dbo.POItemLot.BoxSN AS BoxSN, dbo.StockLoc.LotSN, dbo.stockLOC.StockLocation AS 库位  "+
////         "FROM dbo.Lot inner join dbo.stockLOC on dbo.lot.LotSN=dbo.StockLoc.LotSN	 inner join dbo.POItemLot  on  dbo.stockloc.LotSN=dbo.POItemLot.LotSN");
//	//	soap.addWsProperty("SQLString", "insert into POItem(POId,ProductId,ItemQtyRequired,ItemQtyReceived,ItemQtyRejected) values('D93BC38A-EB0','PRD100006E6K',20,20,0)");
    //   soap.addWsProperty("SQLString", "if SUBSTRING('"+lotSn+"',1,1) <>'M'  begin select '条码有误,请检查!' as BoxSN,'' LotSN,'' 库位   end else begin "+
			//    "SELECT  TOP (100) PERCENT dbo.POItemLot.BoxSN AS BoxSN, dbo.StockLoc.LotSN, dbo.stockLOC.StockLocation AS 库位  FROM  dbo.Lot inner join dbo.stockLOC on dbo.lot.LotSN=dbo.StockLoc.LotSN inner join dbo.POItemLot  on  dbo.stockloc.LotSN=dbo.POItemLot.LotSN  WHERE dbo.Lot.LotSN < '"+lotSn+"' and dbo.stockloc.lotSN not in (select l.lotSN from lot l " +
			    //		" inner join LotNumber ln on l.LotId = ln.LotId " +
			    	//	" ) ORDER BY dbo.StockLoc.Createdate end " );
		//String checkStr = "SORT00000AK7";
		String lineNo = "S02";
		String stationNo = "2";
		String slotNo = "1-10-L";
		soap.addWsProperty("SQLString","declare @retVal int declare @outDetail nvarchar(max) declare @excep nvarchar(100)  exec @retVal = Txn_MaterialsRewind '',@outDetail output,@excep output, " +
					" '','','"+MyApplication.getMseUser().getUserId()+"','"+MyApplication.getMseUser().getUserName()+"','','','','','','','','','' select @outDetail");
		//soap.addWsProperty("SQLString","select s.SOId,s.SO.Name,sr.SORootName from  SORoot sr inner join SO s on sr.SORootID = s.SORootID  where sr.SORootID = '"+checkStr+"';");
//		
     //   soap.addWsProperty("SQLString", "select l.lotSN from lot l " +" inner join LotNumber ln on l.LotId = ln.LotId" );
//		soap.addWsProperty("SQLString","declare @ReturnMessage nvarchar(max) declare @res int declare @excep nvarchar(100) exec @res = Txn_WMSLotQtyChange '',@ReturnMessage output,@excep output,'1'" +
//				",'','','','','','','','','','','Mxxxxxx5',21  select @res,@ReturnMessage ");
		
//" insert into MatercollectLot(POItemLotID,LotID,LotSN,DeliverySN) values('2F9500CF-2F8','FF3D16E9-60F','Mxxxxxx7','Rxxxxx7');");
		SoapSerializationEnvelope envelop = WebService.getWSReqRes(soap);
     Log.i(TAG, ""+envelop.bodyIn);
      List<String>  dataSet = MesWebService.WsDataParser.getResDatSet(envelop.bodyIn.toString());
      List<HashMap<String,String>> lisMaps = MesWebService.getResMapsLis(dataSet);
      String ONE = "SLotNO";
      String TWO = "SMTLineNO";
      String THREE = "StationNO";
      String FOUR = "SLotNO";
      for(int i =0;i<lisMaps.size();i++)
      {
    	  HashMap<String,String> mapItem = lisMaps.get(i);
    	  Log.i(TAG,mapItem.get(ONE)+"  "+mapItem.get(TWO)+" "+mapItem.get(THREE)+" "+mapItem.get(FOUR));
      }
      
      //  Log.i(TAG, envelop.bodyIn+"");
		//Editor editor =	this.getContext().getSharedPreferences("log",this.getContext().MODE_PRIVATE).edit();
		
		//editor.putString("log", envelop.bodyIn.toString());
		//editor.commit();
	}
	
	
	public void testByteToStr()throws Exception
	{
		
		byte[] bytes = LaserScanOperator.ERROR_BARCODEBYTE;
		byte[] srcBytes = "sa11111".getBytes("UTF-8");
		byte[] srcBytes1 = "sa111 11".getBytes("UTF-8");
		byte[] srcBytes2 = "28394".getBytes("UTF-8");
		byte[] srcBytes3 = "364GD64644".getBytes("UTF-8");
		//Log.i(TAG, new String(bytes,"UTF-8")+""+new String(bytes,"UTF-8").length());
		//Log.i(tag, msg)
		Log.i(TAG, HexStrConver.isContainByte(bytes,srcBytes)+"");
		Log.i(TAG, HexStrConver.isContainByte(bytes,srcBytes1)+"");
		Log.i(TAG, HexStrConver.isContainByte(bytes,srcBytes2)+"");
		Log.i(TAG, HexStrConver.isContainByte(bytes,srcBytes3)+"");
		Log.i(TAG, HexStrConver.isContainByte(bytes,LaserScanOperator.ERROR_BARCODEBYTE)+"");
	}
	
	public void testGetEmptySoap() throws Exception
	{
//		Soap soap = MesSoapParser.getSoap();
//		Log.i(TAG, soap.getWsMethod());
//		Log.i(TAG, soap.getWsNameSpa());
//		Log.i(TAG, soap.getWsWsdl());
//		Log.i(TAG, soap.getSoapEdi()+"");
//		Soap soap = MesSoapParser.getOfficalSoap();
//		//soap.addWsProperty("UserTicket", "s");
//		Log.i(TAG, ""+soap.getWsNameSpa());
//		Log.i(TAG, ""+soap.getWsWsdl());
//		Log.i(TAG, ""+soap.getSoapEdi());
//		Log.i(TAG, ""+soap.getWsMethod());
//		Log.i(TAG, ""+soap.isDotNet());
//		Log.i(TAG, ""+soap.getWsProperty().get("UserTicket").equals(""));
		MESUpdateModel model = new MESUpdateModel();
		MESUpdate mesUpdate = model.getLastestMesUpdate();
		Log.i(TAG, ""+mesUpdate.getAppName()+" "+mesUpdate.getAppUpdateURL()+" "+mesUpdate.getAppVersion());
		
	}
	
	
}
