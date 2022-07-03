FOR %%A IN (%*) DO (
	REG ADD "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Enum\USB\VID_18EF&PID_E015\%%~A\Device Parameters" /v EnhancedPowerManagementEnabled /t REG_DWORD /d 00000000 /f
)