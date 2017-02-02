package protocol_whatsapp;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;


public class Group
{
	private String name;
	private List<String> phones; 	

	public Group(String name)
	{
		this.name = name;
		phones = Collections.synchronizedList(new ArrayList<String>());
	}

	public String getName()
	{
		return name;
	}

	public synchronized void addPhone(String phone)
	{
		if (!phones.contains(phone))
			phones.add(phone);
	}

	public synchronized void removePhone(String phone)
	{
		if (phones.contains(phone))
			phones.remove(phone);
	}
	
	public boolean contains(String phone)
	{
		return phones.contains(phone);
	}

	public String toString()
	{
		String groupPhones = "";
		for(String phone : phones)
		{
			if (groupPhones.length()>0)
				groupPhones += ",";

			groupPhones += phone;
		}
		return groupPhones;
	}
}