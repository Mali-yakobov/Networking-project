package protocol_whatsapp;

public class User
{
	private String name;
	private String phone;

	public User(String name, String phone)
	{
		this.name = name;
		this.phone = phone;
	}

	public String getName()
	{
		return name;		
	}

	public String getPhone()
	{
		return phone;
	}

	public String toString()
	{
		return name+"@"+phone;
	}
}