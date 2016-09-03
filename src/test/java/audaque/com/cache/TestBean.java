package audaque.com.cache;

public class TestBean {

	private int age ;
	private String name ;
	private String address;
	private String tel ;
	public TestBean() {
	}
	public TestBean(int age, String name, String address, String tel) {
		super();
		this.age = age;
		this.name = name;
		this.address = address;
		this.tel = tel;
	}

	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
}
