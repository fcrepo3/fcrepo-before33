////////////////////////////////////////////////////////////////////// 
// Fedora Service Stub Code 
// (For Running Fedora Server as a Windows Service)
//////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <windows.h>
#include <winbase.h>
#include <winsvc.h>
#include <process.h>


const int nBufferSize = 2000;
char pServiceName[nBufferSize+1];
char pExeFile[nBufferSize+1];
char pInitFile[nBufferSize+1];
char pLogFile[nBufferSize+1];
char pLogBuffer[nBufferSize+1];
int nProcCount = 0;
int nShutdownCount = 0;
PROCESS_INFORMATION* pProcInfo = 0;
HANDLE * pProcHandles = NULL;

SERVICE_STATUS          serviceStatus; 
SERVICE_STATUS_HANDLE   hServiceStatusHandle; 

VOID WINAPI FedoraServiceMain( DWORD dwArgc, LPTSTR *lpszArgv );
VOID WINAPI FedoraServiceHandler( DWORD fdwControl );

CRITICAL_SECTION myCS;

BOOL GetPrivateProfileBoolean(char* appName, char* keyname, BOOL defaultVal, char* filename)
{
	int val = GetPrivateProfileInt(appName, keyname, defaultVal ? 1 : 0, filename);
	return(val != 0 ? TRUE : FALSE);
}

void WriteLog(char* pFile, BOOL print, char* pMsg)
{
	// write error or other information into log file
	::EnterCriticalSection(&myCS);
	try
	{
//		SYSTEMTIME oT;
//		::GetLocalTime(&oT);
		FILE* pLog = fopen(pFile,"a");
//		fprintf(pLog,"%02d/%02d/%04d, %02d:%02d:%02d\n    %s",oT.wMonth,oT.wDay,oT.wYear,oT.wHour,oT.wMinute,oT.wSecond,pMsg); 
		fprintf(pLog,"%s",pMsg); 
		if (print)  printf("%s", pMsg);
		fclose(pLog);
	} catch(...) {}
	::LeaveCriticalSection(&myCS);
}
void WriteLog1s(char* pFile, BOOL print, char* pMsg, char* parm1)
{
	sprintf(pLogBuffer, pMsg, parm1);
	WriteLog(pFile, print, pLogBuffer);
}

void WriteLog1d(char* pFile, BOOL print, char* pMsg, int parm1)
{
	sprintf(pLogBuffer, pMsg, parm1);
	WriteLog(pFile, print, pLogBuffer);
}

void WriteLog1s1d(char* pFile, BOOL print, char* pMsg, char* parm1, int parm2)
{
	sprintf(pLogBuffer, pMsg, parm1, parm2);
	WriteLog(pFile, print, pLogBuffer);
}


////////////////////////////////////////////////////////////////////// 
//
// Configuration Data and Tables
//

SERVICE_TABLE_ENTRY   DispatchTable[] = 
{ 
	{pServiceName, FedoraServiceMain}, 
	{NULL, NULL}
};

char* ExpandCommandLine(char* pCommandLine)
{
	char buffer[nBufferSize];
	static char Lookup[nBufferSize];
	int bPtr = 0;
	unsigned int i = 0;
	unsigned int len = strlen(pCommandLine);
	while (i <= len)
	{
		if (pCommandLine[i] != '%')
		{
			buffer[bPtr++] = pCommandLine[i++];
		}
		else
		{
			int seglen = strchr(pCommandLine+i+1, '%') - (pCommandLine+i+1);
			pCommandLine[i+seglen+1] = '\0';
			GetPrivateProfileString("Settings", pCommandLine+i+1, "###", Lookup, nBufferSize, pInitFile);
			pCommandLine[i+seglen+1] = '%';
			i += seglen + 2;
			strcpy(buffer+bPtr, Lookup);
			bPtr += strlen(Lookup);
		}
	}
	strcpy(pCommandLine, buffer);
	if (strchr(pCommandLine, '%') != NULL)
	{
		return(ExpandCommandLine(pCommandLine));
	}
	return(pCommandLine);
}

// helper functions
BOOL ExecProcess(int nIndex, char* pCommandLine, int nPauseStart, BOOL bTrackOutput, 
				 BOOL bLogOutput, char* pWorkingDir, PROCESS_INFORMATION* pProcInfo, 
				 DWORD dServiceStatus)
{
	STARTUPINFO startUpInfo = { sizeof(STARTUPINFO),NULL,"",NULL,0,0,0,0,0,0,0,STARTF_USESHOWWINDOW,0,0,NULL,0,0,0};  
	HDESK hCurrentDesktop = GetThreadDesktop(GetCurrentThreadId());
	DWORD len;
	char CurrentDesktopName[512];
	pCommandLine = ExpandCommandLine(pCommandLine);
	GetUserObjectInformation(hCurrentDesktop,UOI_NAME,CurrentDesktopName,MAX_PATH,&len);
	startUpInfo.wShowWindow = SW_HIDE;
	startUpInfo.lpDesktop = CurrentDesktopName;
	if (bTrackOutput || bLogOutput)
	{
		SECURITY_ATTRIBUTES saAttr; 
		 
		// Set the bInheritHandle flag so pipe handles are inherited. 
		 
		saAttr.nLength = sizeof(SECURITY_ATTRIBUTES); 
		saAttr.bInheritHandle = TRUE; 
		saAttr.lpSecurityDescriptor = NULL; 

		HANDLE hChildStdinRd, hChildStdinWr,  
				hChildStdoutRd, hChildStdoutWr;
		// Create a pipe for the child process's STDOUT. 
		 
		if (! CreatePipe(&hChildStdoutRd, &hChildStdoutWr, &saAttr, 0)) 
		{
			WriteLog(pLogFile, FALSE, "Stdout pipe creation failed\n");
		}
		// Ensure the read handle to the pipe for STDOUT is not inherited.

		SetHandleInformation( hChildStdoutRd, HANDLE_FLAG_INHERIT, 0);

		// Create a pipe for the child process's STDIN. 
		 
		if (! CreatePipe(&hChildStdinRd, &hChildStdinWr, &saAttr, 0)) 
		{
			WriteLog(pLogFile, FALSE, "Stdin pipe creation failed\n");
		}

		// Ensure the write handle to the pipe for STDIN is not inherited. 
		 
		SetHandleInformation( hChildStdinWr, HANDLE_FLAG_INHERIT, 0);
		startUpInfo.hStdError = hChildStdoutWr;
		startUpInfo.hStdOutput = hChildStdoutWr;
		startUpInfo.hStdInput = hChildStdinRd;
		startUpInfo.dwFlags |= STARTF_USESTDHANDLES;

		pProcHandles[nIndex*2+0] = hChildStdinWr;  // typically not used.
		pProcHandles[nIndex*2+1] = hChildStdoutRd; 
	}
	// create the process
	//WriteLog1s(pLogFile, "command line = %s\n", pCommandLine);

	if(CreateProcess(NULL,pCommandLine,NULL,NULL,TRUE,NORMAL_PRIORITY_CLASS,NULL,strlen(pWorkingDir)==0?NULL:pWorkingDir,&startUpInfo,pProcInfo))
	{
		// Initialization complete - report running status 
		serviceStatus.dwCurrentState       = dServiceStatus; 
		serviceStatus.dwCheckPoint         = nIndex; 
		serviceStatus.dwWaitHint           = (nPauseStart*6)/5;  
		if(!SetServiceStatus(hServiceStatusHandle, &serviceStatus)) 
		{ 
			WriteLog1d(pLogFile, FALSE, "SetServiceStatus failed, error code = %d\n", GetLastError());
	    } 
		for (int i = 0; i < 10; i++)
		{
//			WriteLog1d(pLogFile, FALSE, "Sleeping for = %d mSecs\n", nPauseStart/10 );
			::Sleep(nPauseStart/10);
			DWORD dwCode;
			if(::GetExitCodeProcess(pProcInfo->hProcess, &dwCode))
			{
				if (dwCode != STILL_ACTIVE)
				{
					break;
				}
				else
				{
//					WriteLog1d(pLogFile, FALSE, "Process %d : Still Active\n", nIndex );
				}
			}
			else
			{
				WriteLog1s1d(pLogFile, FALSE, "Failed to start program '%s', error code = %d\n", pCommandLine, GetLastError()); 
			}
		}
		return TRUE;
	}
	else
	{
		WriteLog1s1d(pLogFile, FALSE, "Failed to start program '%s', error code = %d\n", pCommandLine, GetLastError()); 
		return FALSE;
	}
}

BOOL StartProcess(int nIndex) 
{ 
	// start a process with given index
	char pItem[nBufferSize+1];
	char pWorkingDir[nBufferSize+1];
	char pCommandLine[nBufferSize+1];
	sprintf(pItem,"Process%d\0",nIndex);
	GetPrivateProfileString(pItem,"CommandLine","",pCommandLine,nBufferSize,pInitFile);
	BOOL bTrackOutput = GetPrivateProfileBoolean(pItem,"TrackOutput",FALSE,pInitFile);
	BOOL bLogOutput = GetPrivateProfileBoolean(pItem,"LogOutput",FALSE,pInitFile);
	GetPrivateProfileString("Settings","FEDORA_HOME","",pWorkingDir,nBufferSize,pInitFile);
	int nPauseStart = GetPrivateProfileInt(pItem,"PauseStart",100,pInitFile);

	return ExecProcess(nIndex, pCommandLine, nPauseStart, bTrackOutput, bLogOutput, pWorkingDir, &pProcInfo[nIndex], SERVICE_START_PENDING);
}

BOOL EndProcess(int nIndex) 
{	
	// end a program started by the service
	char pItem[nBufferSize+1];
	char pWorkingDir[nBufferSize+1];
	char pCommandLine[nBufferSize+1];
	PROCESS_INFORMATION tmpProcess;
	sprintf(pItem,"Shutdown%d\0",nIndex);
	GetPrivateProfileString(pItem,"CommandLine","",pCommandLine,nBufferSize,pInitFile);
	GetPrivateProfileString("Settings","FEDORA_HOME","",pWorkingDir,nBufferSize,pInitFile);
	int nPauseStart = GetPrivateProfileInt(pItem,"PauseStart",100,pInitFile);

	return ExecProcess(nIndex, pCommandLine, nPauseStart, FALSE, FALSE, pWorkingDir, &tmpProcess, SERVICE_STOP_PENDING);
}

BOOL KillService() 
{ 
	// kill service with given name
	SC_HANDLE schSCManager = OpenSCManager( NULL, NULL, SC_MANAGER_ALL_ACCESS); 
	if (schSCManager==0) 
	{
		WriteLog1d(pLogFile, TRUE, "OpenSCManager failed, error code = %d\n", GetLastError());
	}
	else
	{
		// open the service
		SC_HANDLE schService = OpenService( schSCManager, pServiceName, SERVICE_ALL_ACCESS);
		if (schService==0) 
		{
			WriteLog1d(pLogFile, TRUE, "OpenService failed, error code = %d\n", GetLastError());
		}
		else
		{
			// call ControlService to kill the given service
			SERVICE_STATUS status;
			if(ControlService(schService,SERVICE_CONTROL_STOP,&status))
			{
				CloseServiceHandle(schService); 
				CloseServiceHandle(schSCManager); 
				return TRUE;
			}
			else
			{
				WriteLog1d(pLogFile, TRUE, "ControlService failed, error code = %d\n", GetLastError());
			}
			CloseServiceHandle(schService); 
		}
		CloseServiceHandle(schSCManager); 
	}
	return FALSE;
}

BOOL RunService() 
{ 
	// run service with given name
	SC_HANDLE schSCManager = OpenSCManager( NULL, NULL, SC_MANAGER_ALL_ACCESS); 
	if (schSCManager==0) 
	{
		WriteLog1d(pLogFile, TRUE, "OpenSCManager failed, error code = %d\n", GetLastError());
	}
	else
	{
		// open the service
		SC_HANDLE schService = OpenService( schSCManager, pServiceName, SERVICE_ALL_ACCESS);
		if (schService==0) 
		{
			WriteLog1d(pLogFile, TRUE, "OpenService failed, error code = %d\n", GetLastError());
		}
		else
		{
			// call StartService to run the service
			if(StartService(schService,0,(const char**)NULL))
			{
				CloseServiceHandle(schService); 
				CloseServiceHandle(schSCManager); 
				return TRUE;
			}
			else
			{
				WriteLog1d(pLogFile, TRUE, "StartService failed, error code = %d\n", GetLastError());
			}
			CloseServiceHandle(schService); 
		}
		CloseServiceHandle(schSCManager); 
	}
	return FALSE;
}

BOOL ShutdownService()
{
	BOOL result = TRUE;
	for(int i = 0; i < nShutdownCount; i++)
	{
		WriteLog1d(pLogFile, FALSE, "Starting shutdown part %d\n", i);
		if (! EndProcess(i))  result = FALSE;
		WriteLog1d(pLogFile, FALSE, "Done with shutdown part %d\n", i);
	}
	return(result);
}

////////////////////////////////////////////////////////////////////// 
//
// This routine gets used to start your service
//
VOID WINAPI FedoraServiceMain( DWORD dwArgc, LPTSTR *lpszArgv )
{
	DWORD   status = 0; 
    DWORD   specificError = 0xfffffff; 
 
    SetProcessShutdownParameters(0x3ff, 0);
	
	serviceStatus.dwServiceType        = SERVICE_WIN32; 
    serviceStatus.dwCurrentState       = SERVICE_START_PENDING; 
    serviceStatus.dwControlsAccepted   = SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN; 
    serviceStatus.dwWin32ExitCode      = 0; 
    serviceStatus.dwServiceSpecificExitCode = 0; 
    serviceStatus.dwCheckPoint         = 0; 
    serviceStatus.dwWaitHint           = 30000; 
 
    hServiceStatusHandle = RegisterServiceCtrlHandler(pServiceName, FedoraServiceHandler); 
    if (hServiceStatusHandle==0) 
    {
		WriteLog1d(pLogFile, FALSE, "RegisterServiceCtrlHandler failed, error code = %d\n", GetLastError());
        return; 
    } 
 
	BOOL result = TRUE;
	for(int i=0;i<nProcCount;i++)
	{
		pProcInfo[i].hProcess = 0;
		WriteLog1d(pLogFile, FALSE,"Starting Process %d\n", i);
		if (! StartProcess(i))  result = FALSE;
		WriteLog1d(pLogFile, FALSE, "Started Process %d\n", i);
	}
    
	// Initialization complete - report running status 
	serviceStatus.dwCurrentState       = result ? SERVICE_RUNNING : SERVICE_STOPPED;
    serviceStatus.dwCheckPoint         = 0; 
    serviceStatus.dwWaitHint           = 0;  
    if(!SetServiceStatus(hServiceStatusHandle, &serviceStatus)) 
    { 
		WriteLog1d(pLogFile, FALSE, "SetServiceStatus failed, error code = %d\n", GetLastError());
    } 
}

////////////////////////////////////////////////////////////////////// 
//
// This routine responds to events concerning your service, like start/stop
//
VOID WINAPI FedoraServiceHandler(DWORD fdwControl)
{
	switch(fdwControl) 
	{
		case SERVICE_CONTROL_STOP:
		case SERVICE_CONTROL_SHUTDOWN:
			serviceStatus.dwWin32ExitCode = 0; 
			serviceStatus.dwCurrentState  = SERVICE_STOP_PENDING; 
			serviceStatus.dwCheckPoint    = 0; 
			serviceStatus.dwWaitHint      = 0;
			// terminate all processes started by this service before shutdown
			{
				if (!SetServiceStatus(hServiceStatusHandle, &serviceStatus))
				{ 
					WriteLog1d(pLogFile, FALSE, "SetServiceStatus failed, error code = %d\n", GetLastError());
				}
				
				WriteLog1s(pLogFile, FALSE, "Received  %s Message in FedoraService\n", (fdwControl == SERVICE_CONTROL_STOP? "STOP" : "SHUTDOWN"));
				ShutdownService();

				serviceStatus.dwWin32ExitCode = 0; 
				serviceStatus.dwCurrentState  = SERVICE_STOPPED; 
				serviceStatus.dwCheckPoint    = 0; 
				serviceStatus.dwWaitHint      = 0;
				if (!SetServiceStatus(hServiceStatusHandle, &serviceStatus))
				{ 
					WriteLog1d(pLogFile, FALSE, "SetServiceStatus failed, error code = %d\n", GetLastError());
				}
			}
			return; 
		case SERVICE_CONTROL_PAUSE:
			serviceStatus.dwCurrentState = SERVICE_PAUSED; 
			break;
		case SERVICE_CONTROL_CONTINUE:
			serviceStatus.dwCurrentState = SERVICE_RUNNING; 
			break;
		case SERVICE_CONTROL_INTERROGATE:
			break;
		default: 
			// bounce processes started by this service
			{
				WriteLog1d(pLogFile, FALSE, "Unrecognized opcode %d\n", fdwControl);
			}
	};
    if (!SetServiceStatus(hServiceStatusHandle,  &serviceStatus)) 
	{ 
		WriteLog1d(pLogFile, FALSE, "SetServiceStatus failed, error code = %d\n", GetLastError());
    } 
}


////////////////////////////////////////////////////////////////////// 
//
// Check Service Status
//
DWORD CheckServiceStatus(char* pName, int sleepTimeSecs)
{
	SERVICE_STATUS_PROCESS statusBuf;
	if (sleepTimeSecs > 0)
	{
		Sleep(sleepTimeSecs*1000);
	}
	SC_HANDLE schSCManager = OpenSCManager( NULL, NULL, SC_MANAGER_ALL_ACCESS); 
	if (schSCManager==0) 
	{
		WriteLog1d(pLogFile, TRUE, "OpenSCManager failed, error code = %d\n", GetLastError());
	}
	else
	{
		SC_HANDLE schService = OpenService( schSCManager, pName, SERVICE_ALL_ACCESS);
		if (schService==0) 
		{
			WriteLog1d(pLogFile, TRUE, "OpenService failed, error code = %d\n", GetLastError());
		}
		else
		{
			DWORD bytesNeeded;
			if(!QueryServiceStatusEx(schService, SC_STATUS_PROCESS_INFO, (BYTE*)&statusBuf, sizeof(statusBuf), &bytesNeeded))
			{
				WriteLog(pLogFile, TRUE, "Failed to Check Service status\n");
			}
			CloseServiceHandle(schService); 
		}
		CloseServiceHandle(schSCManager);	
	}
	return(statusBuf.dwCurrentState);
}


////////////////////////////////////////////////////////////////////// 
//
// Uninstall
//
VOID UnInstall(char* pName)
{
	SC_HANDLE schSCManager = OpenSCManager( NULL, NULL, SC_MANAGER_ALL_ACCESS); 
	if (schSCManager==0) 
	{
		WriteLog1d(pLogFile, TRUE, "OpenSCManager failed, error code = %d\n", GetLastError());
	}
	else
	{
		SC_HANDLE schService = OpenService( schSCManager, pName, SERVICE_ALL_ACCESS);
		if (schService==0) 
		{
			WriteLog1d(pLogFile, TRUE, "OpenService failed, error code = %d\n", GetLastError());
		}
		else
		{
			if(!DeleteService(schService)) 
			{
				WriteLog1s(pLogFile, TRUE, "Failed to uninstall service %s\n", pName);
			}
			else 
			{
				WriteLog1s(pLogFile, TRUE, "Service %s uninstalled\n",pName);
			}
			CloseServiceHandle(schService); 
		}
		CloseServiceHandle(schSCManager);	
	}
}

////////////////////////////////////////////////////////////////////// 
//
// Install
//
VOID Install(char* pPath, char* pName) 
{  
	char* fedHome = getenv("FEDORA_HOME");
	if (fedHome == NULL)
	{
		WriteLog(pLogFile, TRUE, "Error Environment variable FEDORA_HOME must be correctly defined to install fedora as a service");
		return;
	}
	char* javaHome = getenv("JAVA_HOME");
	if (javaHome == NULL)
	{
		WriteLog(pLogFile, TRUE, "Error Environment variable JAVA_HOME must be correctly defined to install fedora as a service");
		return;
	}

	WritePrivateProfileString("Settings","FEDORA_HOME", fedHome, pInitFile);
	WritePrivateProfileString("Settings","JAVA_HOME", javaHome, pInitFile);

	SC_HANDLE schSCManager = OpenSCManager( NULL, NULL, SC_MANAGER_CREATE_SERVICE); 
	if (schSCManager==0) 
	{
		WriteLog1d(pLogFile, TRUE, "OpenSCManager failed, error code = %d\n", GetLastError());
	}
	else
	{
		SC_HANDLE schService = CreateService
		( 
			schSCManager,	/* SCManager database      */ 
			pName,			/* name of service         */ 
			pName,			/* service name to display */ 
			SERVICE_ALL_ACCESS,        /* desired access          */ 
			SERVICE_WIN32_OWN_PROCESS|SERVICE_INTERACTIVE_PROCESS , /* service type            */ 
			SERVICE_AUTO_START,      /* start type              */ 
			SERVICE_ERROR_NORMAL,      /* error control type      */ 
			pPath,			/* service's binary        */ 
			NULL,                      /* no load ordering group  */ 
			NULL,                      /* no tag identifier       */ 
			NULL,                      /* no dependencies         */ 
			NULL,                      /* LocalSystem account     */ 
			NULL
		);                     /* no password             */ 
		if (schService==0) 
		{
			WriteLog1s1d(pLogFile, TRUE, "Failed to create service %s, error code = %d\n", pName, GetLastError());
		}
		else
		{
			WriteLog1s(pLogFile, TRUE, "Service %s installed\n", pName);
			CloseServiceHandle(schService); 
		}
		CloseServiceHandle(schSCManager);
	}	
}

void WorkerProc(void* pParam)
{
	static char readBuffer[10000];
	int nCheckProcess = GetPrivateProfileInt("Settings","CheckProcess",60,pInitFile);

	while(nCheckProcess>0&&nProcCount>0)
	{
		::Sleep(1000*nCheckProcess);
		for(int i=0;i<nProcCount;i++)
		{
			char pItem[nBufferSize+1];
			sprintf(pItem,"Process%d\0",i);
			BOOL logOutput = GetPrivateProfileBoolean(pItem,"LogOutput",FALSE,pInitFile);
			BOOL trackProcess = GetPrivateProfileBoolean(pItem,"TrackProcess",FALSE,pInitFile);
			if(logOutput)
			{
				DWORD dwCode;
				if(pProcInfo[i].hProcess != 0)
				{
					if(::GetExitCodeProcess(pProcInfo[i].hProcess, &dwCode))
					{
						if(dwCode != STILL_ACTIVE)
						{
							if(trackProcess)
							{
								serviceStatus.dwWin32ExitCode = 0; 
								serviceStatus.dwCurrentState  = SERVICE_STOPPED; 
								serviceStatus.dwCheckPoint    = 0; 
								serviceStatus.dwWaitHint      = 0;
								// terminate all processes started by this service before shutdown
								if (!SetServiceStatus(hServiceStatusHandle, &serviceStatus))
								{ 
									WriteLog1d(pLogFile, FALSE, "SetServiceStatus failed, error code = %d\n", GetLastError());
								}
							}
							else
							{
								continue;
							}
						}
					}
					else
					{
						WriteLog1d(pLogFile, FALSE, "GetExitCodeProcess failed, error code = %d\n", GetLastError());
					}
				}
				DWORD nTotalAvail;
				if(pProcHandles[i*2+1] != NULL &&
					::PeekNamedPipe(pProcHandles[i*2+1],NULL,0, NULL, &nTotalAvail, NULL))
				{
					DWORD nNumRead;
					if (nTotalAvail > 0)
					{
						if (::ReadFile(pProcHandles[i*2+1], readBuffer, nTotalAvail, &nNumRead, NULL))
						{
							readBuffer[nNumRead] = '\0';
							WriteLog(pLogFile, FALSE, readBuffer);
						}
					}				
				}
			}
		}
	}
}

////////////////////////////////////////////////////////////////////// 
//
// Standard C Main
//
void main(int argc, char *argv[] )
{
	// initialize global critical section
	::InitializeCriticalSection(&myCS);
	// initialize variables for .exe, .ini, and .log file names
	char pModuleFile[nBufferSize+1];
	DWORD dwSize = GetModuleFileName(NULL,pModuleFile,nBufferSize);
	pModuleFile[dwSize] = 0;
	if(dwSize > 4 && pModuleFile[dwSize-4]=='.')
	{
		sprintf(pExeFile,"%s",pModuleFile);
		char* bSlashPtr = strrchr(pModuleFile, '\\');
		if (bSlashPtr != NULL)
		{
			*bSlashPtr++ = '\0';
		}
		char* bSlashPtr2 = strrchr(pModuleFile, '\\');
		if (bSlashPtr2 != NULL)
		{
			*bSlashPtr2++ = '\0';
		}
		pModuleFile[dwSize-4] = '\0';
		sprintf(pInitFile,"%s\\config\\%s.ini",pModuleFile, bSlashPtr);
		sprintf(pLogFile,"%s\\logs\\%s.log",pModuleFile, bSlashPtr);
	}
	else
	{
		sprintf(pExeFile,"%s",argv[0]);
		sprintf(pInitFile,"%s","FedoraService.ini");
		sprintf(pLogFile,"%s","FedoraService.log");
	}
	// read service name from .ini file
	GetPrivateProfileString("Settings","ServiceName","FedoraService",pServiceName,nBufferSize,pInitFile);
	// read program count from .ini file	
	nProcCount = GetPrivateProfileInt("Settings","ProcCount",0,pInitFile);
	// read shutdown count from .ini file
	nShutdownCount = GetPrivateProfileInt("Settings","ShutdownCount",0,pInitFile);
	// initialize process information array
	if(nProcCount > 0)
	{
		pProcInfo = new PROCESS_INFORMATION[nProcCount];
		pProcHandles = new HANDLE[nProcCount*2];
		for (int i = 0; i < nProcCount; i++)
		{
			pProcInfo[i].hProcess = 0;
			pProcHandles[i*2+0] = 0;
			pProcHandles[i*2+1] = 0;
		}
	}
	// uninstall service if switch is "-u"
	if(argc==2&&_stricmp("-u",argv[1])==0)
	{
		UnInstall(pServiceName);
	}
	// install service if switch is "-i"
	else if(argc==2&&_stricmp("-i",argv[1])==0)
	{			
		Install(pExeFile, pServiceName);
	}
	// kill the service
	else if(argc==2&&_stricmp("-k",argv[1])==0)
	{
		if(KillService())
		{
			WriteLog1s(pLogFile, TRUE, "Killed service %s.\n", pServiceName);
		}
		else
		{
			WriteLog1s(pLogFile, TRUE, "Failed to kill service %s.\n", pServiceName);
		}
	}
	// run the service 
	else if(argc>=2&&_stricmp("-r",argv[1])==0)
	{
		if(RunService())
		{
			WriteLog1s(pLogFile, TRUE, "Starting Fedora service, check %s for details\n", pLogFile);
		}
		else
		{
			WriteLog1s(pLogFile, TRUE, "Failed to run Fedora service, check %s for details\n", pLogFile);
		}
	}
	else if(argc>=2&&_stricmp("-c",argv[1])==0)
	{
		int sleepTime = 0;
		if (argc >=3 ) sleepTime = atoi(argv[2]);
		int status = CheckServiceStatus(pServiceName, sleepTime);
		exit(status);
	}
	// assume user is starting this service 
	else 
	{
		// start a worker thread to check for dead programs (and restart if necessary)
		if(_beginthread(WorkerProc, 0, NULL)==-1)
		{
			WriteLog1d(pLogFile, FALSE, "_beginthread failed, error code = %d\n", GetLastError());
		}
		// pass dispatch table to service controller
		if(!StartServiceCtrlDispatcher(DispatchTable))
		{
			WriteLog1d(pLogFile, FALSE, "StartServiceCtrlDispatcher failed, error code = %d\n", GetLastError());
		}
		// you don't get here unless the service is shutdown
	}
	// clean up
	delete []pProcInfo;
	delete [] pProcHandles;
	::DeleteCriticalSection(&myCS);
}

