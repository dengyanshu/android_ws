listview �ǰ�׿��������Ҫ����� û��֮һ

listview ��Ҫ����������


�̳���baseadapter  ��Ҫ��дgetview ��getcount����
����getview ���� ����չ��ÿһ�����ݣ�
convertview �ǰ�׿�ṩ�Ļ��� 
if(convertview==null){
   view=new TextView();
}else{
   view=convertview
}
return view


���õ�arrayadapter  ��simpleadapter  �Ƿ�װ�õ� ����Ҫ����ʹ�� �û��Զ�����Լ̳�baseadaper

View.inflater
LayoutInflater
context.getSystemService()
���ַ�ʽ���԰�һ������ ת����һ��view����