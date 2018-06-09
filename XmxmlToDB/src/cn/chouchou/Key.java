package cn.chouchou;

public class Key {
   private  String name;
   private  String privateKey;
   
   private  CertificateChain  certchain;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getPrivateKey() {
	return privateKey;
}

public void setPrivateKey(String privateKey) {
	this.privateKey = privateKey;
}

public CertificateChain getCertchain() {
	return certchain;
}

public void setCertchain(CertificateChain certchain) {
	this.certchain = certchain;
}
   
   
}
