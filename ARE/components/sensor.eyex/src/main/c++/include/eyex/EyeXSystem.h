/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * EyeXSystem.h
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_SYSTEM_API__H__)
#define __TOBII_TX_SYSTEM_API__H__

/*********************************************************************************************************************/

/**
  txInitializeSystem

  Initializes the Tobii EyeX interaction system.
  This function must be called prior to any other in the API, except txEnableMonoCallbacks.
  A client can choose to override the default memory model, threading model and logging model by supplying custom models
  to this function.

  @param flags [in]:
    Specifies which system components to override.
    
  @param pLoggingModel [in]:
    A pointer to a TX_LOGGINGMODEL which will override the default model.
    This argument can be NULL to use the default logging model.
        
  @param pThreadingModel [in]:
    A pointer to a TX_THREADINGMODEL which will override the default model.
    This argument can be NULL to use the default threading model.

  @param pSchedulingModel [in]:
	A pointer to a TX_SCHEDULINGMODEL which will override the default model.
	This argument can be NULL to use the default scheduling model.

  @return 
    TX_RESULT_OK: The system was successfully initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_SYSTEMALREADYINITIALIZED: The system is already initialized.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txInitializeSystem(
    TX_SYSTEMCOMPONENTOVERRIDEFLAGS flags,
    const TX_LOGGINGMODEL* pLoggingModel,
    const TX_THREADINGMODEL* pThreadingModel,
	const TX_SCHEDULINGMODEL* pSchedulingModel
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txUninitializeSystem

  Uninitializes the system.
  If any context is still active this call will fail.
  
  @return 
    TX_RESULT_OK: The system was successfully uninitialized.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_SYSTEMSTILLINUSE: The system is still in use.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txUninitializeSystem();
TX_C_END

/*********************************************************************************************************************/

/**
  txIsSystemInitialized

  Checks if the system has been initialized.
  
  @param pInitialized [out]: 
    A pointer to a TX_BOOL which will be set to true if the system is initialized and false otherwise.
    Must not be NULL.

  @return 
    TX_RESULT_OK: The operation was successful.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txIsSystemInitialized(
    TX_BOOL* pInitialized
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txWriteLogMessage

  Writes a message using the internal logging system. 
  This method is typically not intended for end users but rather for the different language bindings to have a common
  way of utilizing the logging system.

  @param level [in]: 
    The log level for this message.

  @param scope [in]: 
    The scope for this message.

  @param message [in]:
    The log message it self.

  @return
    TX_RESULT_OK: The message was successfully written to the log.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
  */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txWriteLogMessage(
    TX_LOGLEVEL level,
    TX_CONSTSTRING scope,
    TX_CONSTSTRING message
    );
TX_C_END

/*********************************************************************************************************************/
/**
  txSetInvalidArgumentHandler

  Sets a hook that notifies when an invalid argument has been passed to any of the API function.
  This function should typically only be used for testing purposes.

  @param handler [in]: 
    The callback to be invoked when an invalid argument is detected.
	
  @param userParam [in]:
    A TX_USERPARAM which will be provided as a parameter to the callback.
    Can be NULL and will in this case be ignored.

  @return 
    TX_RESULT_OK: The invalid argument handler was successful set.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetInvalidArgumentHandler(
    TX_INVALIDARGUMENTCALLBACK handler,
    TX_USERPARAM userParam
    );
TX_C_END

/*********************************************************************************************************************/
/**
  txEnableMonoCallbacks

  Prepares the EyeX client library for use with the Mono .NET runtime: before a callback function is invoked, the 
  thread on which the callback will be made is attached to a mono domain, and the thread is detached again when the 
  callback function returns. Mono requires that any threads calling managed code be attached for garbage collection 
  and soft debugging to work properly.
  
  This function must be called prior to any other in the API, and from a managed thread. The subsequent callback 
  invocations will be attached to the same mono domain as the caller thread.

  Note that Mono callbacks cannot be used in combination with a custom threading model.

  @param monoModuleName [in]: 
    The name of the Mono runtime module (dll). Typically "mono".
 
  @return 
    TX_RESULT_OK: The mono callbacks were successfully enabled.
    TX_RESULT_INVALIDARGUMENT: The Mono module name could not be used to resolve the necessary Mono functions.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txEnableMonoCallbacks(
	TX_CONSTSTRING monoModuleName
    );
TX_C_END

/*********************************************************************************************************************/
/**
  txGetServerVersionAsync

  Gets the server version asynchronously.

  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context on which to create the command.
    Must not be TX_EMPTY_HANDLE.
	
  @param completionHandler [in]:
    The TX_ASYNCDATACALLBACK that will handle the version request result. The Data property bag
    will contain the servers major, minor and build version.
    	
	That handle to the async data must NOT be released.

  @param userParam [in]:
    A TX_USERPARAM which will be provided as a parameter to the completion callback. 
    Can be NULL.

  @return 
    TX_RESULT_OK: The request was sent successfully.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDCONTEXT: The handle to the context was invalid.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetServerVersionAsync(
	TX_CONTEXTHANDLE hContext,
    TX_ASYNCDATACALLBACK completionHandler,
    TX_USERPARAM userParam
    );
TX_C_END

/*********************************************************************************************************************/


#endif /* !defined(__TOBII_TX_SYSTEM_API__H__) */

/*********************************************************************************************************************/
