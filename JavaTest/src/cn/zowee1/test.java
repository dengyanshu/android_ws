package cn.zowee1;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
             ILanguageServiceService  ws=new ILanguageServiceService();
             ILanguageService  port=ws.getILanguageServicePort();
             System.out.println(port.get(1));
	}

}
