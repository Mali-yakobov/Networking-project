package protocol_whatsapp;

import java.lang.Thread;
import java.util.Dictionary;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class Engine
{
	static List<User> users;
	static List<Group> groups;

	static Map<String, String> tokenMap;
	static Map<String, String> reverseTokenMap;
	static Map<String, List<WhatsAppMessage>>  messages;
	static Map<String, Group> groupMap;

	static boolean initialized = false;	
	static boolean closingState = false;

	private synchronized void initialize()
	{
		if (!initialized)
		{
			users = Collections.synchronizedList(new ArrayList<User>());
			groups = Collections.synchronizedList(new ArrayList<Group>());
			tokenMap = Collections.synchronizedMap(new HashMap<String, String>());
			groupMap = Collections.synchronizedMap(new HashMap<String, Group>());
			reverseTokenMap = Collections.synchronizedMap(new HashMap<String, String>());
			messages = Collections.synchronizedMap(new HashMap<String, List<WhatsAppMessage>>());
			initialized = true;
		}	
	}

	public Engine()
	{
		initialize();
	}

	private static String generateUserAuth()
	{
		return String.format("%016x", System.nanoTime());
	}

	public synchronized static String login(Request request)
	{
		String userName = request.getParam("UserName");
		String phone = request.getParam("Phone");

		if (userName!=null && phone!=null)
		{
			String user_auth = "";

			if (!isClosing())
			{
				User user = new User(userName, phone);


				if (!tokenMap.containsKey(user.toString()))
				{
					user_auth = generateUserAuth();
					tokenMap.put(user.toString(), user_auth);
					reverseTokenMap.put(user_auth, user.toString());
					users.add(user);
				} else {
					user_auth = tokenMap.get(user.toString());
				}

				return Response.create(StatusCode.OK, user_auth, "Welcome "+user.toString());
			} else {
				return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
			}
		} else {
			return Response.create(StatusCode.NOT_ALLOWED, "ERROR 765: Cannot login, missing parameters");
		}
	}

	private static String getPhoneFromUserString(String userString)
	{
		int index = userString.indexOf("@");

		if (index>=0)
			return userString.substring(index+1);
		else
			return "";
	}

	public synchronized static String logout(Request request)
	{
		String user_auth = request.getUserAuth();

		if (reverseTokenMap.containsKey(user_auth))
		{
			String userString = reverseTokenMap.get(user_auth);

			tokenMap.remove(userString);
			reverseTokenMap.remove(user_auth);

			for(int i=0; i<users.size(); i++)
			{
				if (users.get(i).toString().equals(userString))
				{
					users.remove(users.get(i));
				}
			}

			for(int i=0; i<groups.size(); i++)
			{
				groups.get(i).removePhone(getPhoneFromUserString(userString));
			}

			return Response.create(StatusCode.OK, "Goodbye");
		} else {
			return Response.create(StatusCode.NOT_ALLOWED, user_auth, "Not Authorized");	
		}
	}

	public synchronized static String send(Request request)
	{
		String user_auth = request.getUserAuth();

		if (reverseTokenMap.containsKey(user_auth))
		{
			String type = request.getParam("Type");
			String target = request.getParam("Target");
			String content = request.getParam("Content");

			if (type!=null && target!=null && content!=null)
			{

				if (!isClosing())
				{
					if (type.equals("Direct"))
						return sendDirect(user_auth, target, content);
					else if (type.equals("Group"))
						return sendGroup(user_auth, target, content);
					else
						return Response.create(StatusCode.NOT_ALLOWED, "ERROR 836: Invalid Type");
				} else {
					return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
				}
			} else {
				return Response.create(StatusCode.NOT_ALLOWED, "ERROR 711: Cannot send, missing parameters");
			}			
		} else {
			return Response.create(StatusCode.NOT_ALLOWED, user_auth, "Not Authorized");	
		}
	}

	private static void addMessageForUser(String phone, String from, String message)
	{
		List<WhatsAppMessage> messagesList = messages.get(phone);
		if (messagesList==null)
		{
			messagesList = Collections.synchronizedList(new ArrayList<WhatsAppMessage>());
			messages.put(phone, messagesList);
		}
		messagesList.add(new WhatsAppMessage(from, message));
	}

	private static String sendDirect(String user_auth, String target, String message)
	{
		String userString = reverseTokenMap.get(user_auth);
		String fromPhone = getPhoneFromUserString(userString);

		int added = 0;
		if (!isClosing())
		{			
			for(int i=0; i<users.size(); i++)
			{
				if (users.get(i).getPhone().equals(target))
				{
					addMessageForUser(target, fromPhone, message);
					added++;
				}
			}
		} else {
			return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
		}

		if (added>0)
			return Response.create(StatusCode.OK, user_auth, "Message has beet sent");
		else
			return Response.create(StatusCode.NOT_ALLOWED, user_auth, "ERROR 771: Target Does not Exist");
	}

	private static String sendGroup(String user_auth, String target, String message)
	{
		String userString = reverseTokenMap.get(user_auth);
		String fromPhone = getPhoneFromUserString(userString);

		String fromGroup = "";

                for(Group group : groups)
		{
			if (group.contains(fromPhone))
			{
				fromGroup = group.getName();
				break;
			}
		}

		if (fromGroup.length()==0)
			fromGroup = fromPhone;

		if (groupMap.containsKey(target))
		{
			int added = 0;
			if (!isClosing())
			{
				Group group = groupMap.get(target);
				for(int i=0; i<users.size(); i++)
				{
					String targetPhone = users.get(i).getPhone();
					if (group.contains(targetPhone))
					{
						addMessageForUser(targetPhone, fromGroup, message);
						added++;
					}
				}
			} else {
				return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
			}

			if (added>0)
				return Response.create(StatusCode.OK, user_auth, "Message has beet sent");
			else
				return Response.create(StatusCode.NOT_ALLOWED, user_auth, "ERROR 771: Target Does not Exist");
		}
		else 
		{
			return Response.create(StatusCode.NOT_ALLOWED, user_auth, "ERROR 771: Target Does not Exist");
		}
	}

	public synchronized static String queue(Request request)
	{
		String user_auth = request.getUserAuth();

		if (reverseTokenMap.containsKey(user_auth))
		{
			String userString = reverseTokenMap.get(user_auth);
			String toPhone = getPhoneFromUserString(userString);

			List<WhatsAppMessage> messagesList = messages.put(toPhone, Collections.synchronizedList(new ArrayList<WhatsAppMessage>()));

			String messagesContent = "";
			for(WhatsAppMessage msg : messagesList)
			{
				if (messagesContent.length()>0)
					messagesContent += "\n";
				messagesContent += "From:"+msg.getFrom()+"\n";
				messagesContent += "Msg:"+msg.getMessage()+"\n";
			}

			if (messagesContent.length()>0)
				return Response.create(StatusCode.OK, user_auth, messagesContent);	
			else
				return Response.create(StatusCode.OK, user_auth, "No new messages");
		} else {
			return Response.create(StatusCode.NOT_ALLOWED, user_auth, "Not Authorized");	
		}		
	}

	private static boolean isUsedPhone(String phone)
	{
		for(User user : users)
		{
			if (user.getPhone().equals(phone))
				return true;
		}
		return false;
	}

	public synchronized static String create_group(Request request)
	{
		String groupName = request.getParam("GroupName");
		String usersToAdd = request.getParam("Users");

		if (groupName!=null && usersToAdd!=null)
		{
			if (!groupMap.containsKey(groupName))
			{

				if (!isClosing())
				{
					Group group = new Group(groupName);
					String[] phones = usersToAdd.split(",");
					for(int i=0; i<phones.length; i++)
					{
						if (isUsedPhone(phones[i]))
							group.addPhone(phones[i]);
						else
							return Response.create(StatusCode.NOT_ALLOWED, "ERROR 929: Unknown User "+phones[i]);
					}

					groups.add(group);
					groupMap.put(groupName, group);
					return Response.create(StatusCode.OK, "Group "+groupName+" Created");
				}
				else 
				{
					return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
				}
			} else {
				return Response.create(StatusCode.NOT_ALLOWED, "ERROR 511: Group Name Already Taken");
			}
		}
		else {
			return Response.create(StatusCode.NOT_ALLOWED, "ERROR 675: Cannot create group, missing parameters");
		}
	}

	public synchronized static String listUsers()
	{
		if (!isClosing())
		{
			String userContent = ""; 
			for(User user : users)
			{
				if (userContent.length()>0)
					userContent+="\n";
				userContent += user.toString();			
			}

			if (userContent.length()>0)
				return Response.create(StatusCode.OK, userContent);
			else
				return Response.create(StatusCode.OK, "No users");
		}
		else 
		{
			return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
		}
	}

	public synchronized static String listGroups()
	{
		if (!isClosing())
		{
			String groupsContent = ""; 
			for(Group group : groups)
			{
				if (groupsContent.length()>0)
					groupsContent+="\n";
				groupsContent += group.getName()+":"+group.toString();
			}

			if (groupsContent.length()>0)
				return Response.create(StatusCode.OK, groupsContent);
			else
				return Response.create(StatusCode.OK, "No Groups");
		} else {
			return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
		}
	}

	public synchronized static String listGroup(String groupName)
	{		
		if (groupMap.containsKey(groupName))
		{
			return Response.create(StatusCode.OK, groupMap.get(groupName).toString());
		} else		
			return Response.create(StatusCode.NOT_ALLOWED, "ERROR 771: Target Does not Exist");
	}

	public synchronized static String list(Request request)
	{
		String listType = request.getParam("List");

		if (listType!=null)
		{
			if (!isClosing())
			{
				if (listType.equals("Users"))
					return listUsers();
				else if (listType.equals("Groups"))
					return listGroups();
				else 
				{			
					String group = request.getParam("Group");
					if (group!=null)
					{
						return listGroup(group);
					} else {
						return Response.create(StatusCode.NOT_ALLOWED, "ERROR 273: Missing Parameters");
					}
				}
			} else {
				return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
			}
		} else {
			return Response.create(StatusCode.NOT_ALLOWED, "ERROR 273: Missing Parameters");
		}		
	}

	public synchronized static boolean isClosing()
	{
		return closingState;
	}

	public synchronized static String add_user(Request request)
	{
		String target = request.getParam("Target");
		String phone = request.getParam("User");

		if (target!=null && phone!=null)
		{
			if (groupMap.containsKey(target))
			{
				if (!isClosing())
				{
					Group group = groupMap.get(target);
					if (!group.contains(phone))
					{
						group.addPhone(phone);
						return Response.create(StatusCode.OK, phone + " added to "+target);
					} else {
						return Response.create(StatusCode.NOT_ALLOWED, "ERROR 142: Cannot add user, user already in group");
					}
				} else {
					return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
				}
			} else 
				return Response.create(StatusCode.NOT_ALLOWED, "ERROR 770: Target does not exist");

		} else {
			return Response.create(StatusCode.NOT_ALLOWED, "ERROR 242: Cannot add user, missing parameters");
		}
	}

	private static boolean messageAreSent()
	{
		int count = 0;

		for(User user : users)
		{
			count += messages.get(user.getPhone()).size();
		}

		return count>0;
	}

	public static void gracefulClosing()
	{
		if (!closingState)
		{
			for(User user : users)
			{
				addMessageForUser(user.getPhone(), "Server Admin", "Server is shutting down... Automatic logging out...");
			}
			closingState = true;

			try
			{
				for(int i=1; i<=6 && !messageAreSent(); i++)
				{
					System.out.println("Waiting..."+i+" sec");
					Thread.sleep(1000);
				}
			}
			catch(InterruptedException ex)
			{
	                }
		}				
	}

	public synchronized static String remove_user(Request request)
	{
		String target = request.getParam("Target");
		String phone = request.getParam("User");

		if (target!=null && phone!=null)
		{
			if (groupMap.containsKey(target))
			{
				if (!isClosing())
				{
					Group group = groupMap.get(target);
					group.removePhone(phone);
					return Response.create(StatusCode.OK, phone + " removed from "+target);
				} else {
					return Response.create(StatusCode.NOT_ALLOWED, "ERROR 999: Server is in closing state");
				}
			} else 
				return Response.create(StatusCode.NOT_ALLOWED, "ERROR 769: Target does not exist");
		} else {
			return Response.create(StatusCode.NOT_ALLOWED, "ERROR 336: Cannot remove, missing parameters");
		}
	}
}
