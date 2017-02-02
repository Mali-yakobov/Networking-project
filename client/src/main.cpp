#include <cctype>
#include <iomanip>
#include <sstream>
#include <string>
#include <stdio.h>
#include <iostream>

#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>
#include <semaphore.h>

using namespace std;

string user_auth = "";
int port;
string portstr;
string host;

pthread_mutexattr_t mattr;
pthread_mutex_t client_mutex = PTHREAD_MUTEX_INITIALIZER;

string url_encode(const string &value);
string url_decode(string &SRC);

pthread_t client_thread;

string httpOperation(string request)
{
	string response;

	struct addrinfo host_info;
	struct addrinfo *host_info_list;
	memset(&host_info, 0, sizeof(host_info));

	// cout << request << endl;
	   
	host_info.ai_family = AF_UNSPEC;
	host_info.ai_socktype = SOCK_STREAM;

	int status = getaddrinfo(host.c_str(), portstr.c_str(), &host_info, &host_info_list);

	if (status == 0)
	{
		int socketfd;
		socketfd = socket(host_info_list->ai_family, host_info_list->ai_socktype, 
			host_info_list->ai_protocol);

		if (socketfd != -1)
		{	
			status = connect(socketfd, host_info_list->ai_addr, host_info_list->ai_addrlen);
			if (status != -1)
			{
				ssize_t bytes_sent = send(socketfd, request.c_str(), request.size(), 0);


				ssize_t bytes_recieved = 1;
				char incoming_data_buffer[65536];

				do
				{
					bytes_recieved = recv(socketfd, incoming_data_buffer, 16384, 0);
					response += incoming_data_buffer;					
				}while(bytes_recieved>0 && incoming_data_buffer[bytes_recieved-1]!='$');

				//if (bytes_recieved == 0) std::cout << "host shut down." << std::endl;
				//if (bytes_recieved == -1)std::cout << "recieve error!" << std::endl;
			}
			close(socketfd);
		}
		freeaddrinfo(host_info_list);
	}
	
	// cout << response << endl;	
	return response;
}

void check_messages()
{
	int statusCode;
	string version;
	string message;

	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "GET /queue.jsp HTTP/1.1" << endl;
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	string httpresponse = httpOperation(http.str());

	stringstream parsing(httpresponse+"\n\n");
	parsing >> version;
	parsing >> statusCode;
	if (statusCode==200)
	{
		string dummy;
		getline(parsing, message);
		do
		{
			getline(parsing, message);
		}while(message.size()!=0);		
		getline(parsing, message);

		if (message!="No new messages")
		{
			cout << endl << endl << "Received message(s): "<< endl;
			int index = 0;
			do
			{				
				cout << message << endl;				
				index++;
				getline(parsing, message);
			}while(message[0]!='$');		
			cout << endl << "Enter command: " << endl;
		}		
	}
}

void* check_msg_thread_function(void* param)
{
	int finished = 0;
	do
	{
		pthread_mutex_lock(&client_mutex);		
		finished = user_auth.size()==0;
		pthread_mutex_unlock(&client_mutex);
		if (!finished)
		{
			check_messages();
		        usleep(1000000);
		}
	}while(!finished);
}

void handleResponse(string httpresponse, int read_cookie = 1)
{
	int statusCode;
	string version;
	string message;

	stringstream parsing(httpresponse);

	parsing >> version;
	parsing >> statusCode;
	if (statusCode==200)
	{
		string dummy;
		getline(parsing, message);

		if (read_cookie)
		{
			string cookie;
			parsing >> cookie;
			parsing >> cookie;
			pthread_mutex_lock(&client_mutex);		
			user_auth = cookie.substr(10);		
			pthread_mutex_unlock(&client_mutex);
			getline(parsing, message);
		}
		
		do
		{
			getline(parsing, message);
		}while(message.size()!=0);
		getline(parsing, message);
				
		cout << message << endl << endl;
	} else {
		getline(parsing, message);
		do
		{
			getline(parsing, message);
		}while(message.size()!=0);

		getline(parsing, message);
		cout << message << endl << endl;
	}

}

void login(string userName, string phone)
{
	stringstream http;
	http << "POST /login.jsp HTTP/1.1" << endl;	
	http << endl;
	http << "UserName=" << url_encode(userName) << "&Phone=" << url_encode(phone) << endl;
	http << "$";

	cout << endl;
	string httpresponse = httpOperation(http.str());
	handleResponse(httpresponse,1);	

	if (user_auth.size()>0)
	{        	
		int rc = pthread_create(&client_thread, NULL, &check_msg_thread_function, NULL);
	}
}

void logout()
{
	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "GET /logout.jsp HTTP/1.1" << endl;
	http << "Host: " << host << endl;
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	cout << endl;
	string httpresponse = httpOperation(http.str());
	handleResponse(httpresponse,0);

	pthread_mutex_lock(&client_mutex);
	user_auth = "";
	pthread_mutex_unlock(&client_mutex);
}

void list_users()
{
	int statusCode;
	string version;
	string message;

	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "POST /list.jsp HTTP/1.1" << endl;
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "List=" << url_encode("Users") << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	string httpresponse = httpOperation(http.str());

	stringstream parsing(httpresponse+"\n\n");
	parsing >> version;
	parsing >> statusCode;
	if (statusCode==200)
	{
		string dummy;
		getline(parsing, message);
		do
		{
			getline(parsing, message);
		}while(message.size()!=0);		
		getline(parsing, message);				

		do
		{ 			
			cout << message << endl;
			message = "";
			getline(parsing, message);			
		}while(message[0]!='$');
		cout << endl;
	}
}

void list_groups()
{
	int statusCode;
	string version;
	string message;

	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "POST /list.jsp HTTP/1.1" << endl;
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "List=" << url_encode("Groups") << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	string httpresponse = httpOperation(http.str());

	stringstream parsing(httpresponse+"\n\n");
	parsing >> version;
	parsing >> statusCode;
	if (statusCode==200)
	{
		string dummy;
		getline(parsing, message);
		do
		{ 
			getline(parsing, message);
		}while(message.size()!=0);		
		getline(parsing, message);				

		do
		{ 			
			cout << message << endl;
			message = "";
			getline(parsing, message);			
		}while(message[0]!='$');
		cout << endl;
	}
}

void list_group(string group)
{
	int statusCode;
	string version;
	string message;

	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "POST /list.jsp HTTP/1.1" << endl;
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "List=" << url_encode("Group") << "&Group=" << url_encode(group) << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	string httpresponse = httpOperation(http.str());

	stringstream parsing(httpresponse+"\n\n");
	parsing >> version;
	parsing >> statusCode;
	if (statusCode==200)
	{
		string dummy;
		getline(parsing, message);
		do
		{
			getline(parsing, message);
		}while(message.size()!=0);		
		getline(parsing, message);				

		do
		{ 			
			cout << message << endl;
			message = "";
			getline(parsing, message);			
		}while(message[0]!='$');
		cout << endl;
	}
}

void send_group(string group, string message)
{
	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "POST /send.jsp HTTP/1.1" << endl;	
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "Type=Group&Target=" << url_encode(group) << "&Content=" << url_encode(message) << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	cout << endl;
	string httpresponse = httpOperation(http.str());

	handleResponse(httpresponse,1);
}

void send_user(string phone, string message)
{
	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "POST /send.jsp HTTP/1.1" << endl;	
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "Type=Direct&Target=" << url_encode(phone) << "&Content=" << url_encode(message) << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	cout << endl;
	string httpresponse = httpOperation(http.str());
	
	handleResponse(httpresponse,1);
}

void add(string group, string phone)
{
	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "POST /add_user.jsp HTTP/1.1" << endl;	
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "Target=" << url_encode(group) << "&User=" << url_encode(phone) << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	cout << endl;
	string httpresponse = httpOperation(http.str());
	
	handleResponse(httpresponse,0);
}

void remove(string group, string phone)
{
	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "POST /remove_user.jsp HTTP/1.1" << endl;	
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "Target=" << url_encode(group) << "&User=" << url_encode(phone) << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	cout << endl;
	string httpresponse = httpOperation(http.str());
	
	handleResponse(httpresponse,0);
}

void create_group(string group, string users)
{
	pthread_mutex_lock(&client_mutex);
	stringstream http;
	http << "POST /create_group.jsp HTTP/1.1" << endl;	
	http << "Cookie: user_auth=" << user_auth << endl;
	http << endl;
	http << "GroupName=" << url_encode(group) << "&Users=" << url_encode(users) << endl;
	http << "$";
	pthread_mutex_unlock(&client_mutex);

	cout << endl;
	string httpresponse = httpOperation(http.str());
	
	handleResponse(httpresponse,0);
}

int main(int argc, char* argv[])
{
	if (argc>2)
	{
		host = argv[1];
		portstr = argv[2];
		stringstream ss(argv[2]);
		ss >> port;
		int finished = 0;

		do
		{
			string user_input;
			cout << "Enter command: ";
			getline(cin, user_input);

			stringstream ss(user_input);
			string command;
			ss >> command;
		
			if (command=="login")
			{
                                int loggedIn = 0;
				pthread_mutex_lock(&client_mutex);
				loggedIn = user_auth.size()!=0;
				pthread_mutex_unlock(&client_mutex);

				if (!loggedIn)
				{				
					string userName;
					string phone;
	
					ss >> userName;
					ss >> phone;

					login(userName, phone);				
				} else {
					cout << endl << "Already logged in." << endl << endl;
				}
			}
			else if (command=="logout")
			{
				logout();
			}
			else if (command=="create")
			{
				string argument;
				ss >> argument;

				if (argument=="group")
				{
					string group;
					string users;
					ss >> group;
					ss >> users;
					create_group(group, users);
				}
				else 
				{
					cout << "Invalid command" << endl;
				}       	

			}
			else if (command=="list")
			{
				string argument;
				ss >> argument;
				if (argument=="users")
				{
					list_users();
				}
				else if (argument=="groups")
				{
					list_groups();
				}
				else if (argument=="group")
				{
					string group;
					ss >> group;
					list_group(group);
				}
				else 
				{
					cout << "Invalid command" << endl;
				}       	
			}
			else if (command=="send")
			{
				string argument;
				ss >> argument;			

				if (argument=="group")
				{
					string group;
					string message;
					ss >> group;

					getline(ss, message);
					send_group(group, message);
				}
				else if (argument=="user")
				{
					string phone;
					string message;
					ss >> phone;

					getline(ss, message);
					send_user(phone, message);
				}
				else 
				{
					cout << "Invalid command" << endl;
				}
			}
			else if (command=="add")
			{
				string group;
				string phone;
				ss >> phone;
				ss >> group;
				add(group, phone);				
			}
			else if (command=="remove")
			{
				string group;
				string phone;
				ss >> phone;
				ss >> group;
				remove(group, phone);				
			}
			// else if (command=="queue")
			// {
			//	check_messages();
			// }
			else if (command=="exit")
			{
				logout();
				finished = 1;
				usleep(2000000);
			}
			else 
			{
				cout << "Invalid command" << endl;
			}
		}while(!finished);

	} else {
		cout << "Usage: " << endl;
		cout << "client [host] [port]" << endl;
	}

	return 0;
}